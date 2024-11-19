package com.example.test_project.views;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_project.R;
import com.example.test_project.api.WeatherService;
import com.example.test_project.model.WeatherModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.DryZoneRecyclerAdapter;
import com.example.test_project.viewHelper.UserSessionManager;
import com.example.test_project.viewHelper.WeatherClient;
import com.example.test_project.viewHelper.ZoneRecyclerAdapter;
import com.example.test_project.viewModel.DashBoardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author: Sahiram Ravikumar
 * Activity of Dashboard with HubData and ZoneData
 */
public class DashboardActivity extends AppCompatActivity implements ZoneRecyclerAdapter.ItemClickListener, LocationListener {

    private static final String TAG = "Dashboard Activity";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ZoneRecyclerAdapter adapter;
    private int zoneCount;

    private RecyclerView dRecyclerView;
    private LinearLayoutManager dLayoutManager;
    private DryZoneRecyclerAdapter dAdapter;

    private SwitchCompat autoSwitch;
    private Boolean switchStatus;

    private TextView autowater_message;
    private TextView autowater_text;
    private TextView humidity;
    private TextView temperature;
    private RelativeLayout autowater_card;
    private RelativeLayout dash_warn_waterfill_card;
    private RelativeLayout no_zones;
    private ImageButton addButton;
    private ImageView footer_img;

    private DashBoardViewModel dashBoardViewModel;
    private List<ZoneModel> zoneDataList;

    private int hub_Id;

    private UserSessionManager session;

    private LocationManager locationManager;
    private Location location;
    private double latitude, longitude;
    private int mHumidity;
    private double mTemperature;
    private String city;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Session
        session = new UserSessionManager(getApplicationContext());
        if (session.checkLogin())
            finish();

        //MVVM
        dashBoardViewModel = new ViewModelProvider(this).get(DashBoardViewModel.class);

        // default elements
        autoSwitch = findViewById(R.id.autowater_switch);
        autowater_message = findViewById(R.id.autowater_message);
        autowater_text = findViewById(R.id.autowater_text);
        autowater_card = findViewById(R.id.autowater_card);
        addButton = findViewById(R.id.add_icon);
        dash_warn_waterfill_card = findViewById(R.id.dash_warn_waterfill_card);
        footer_img = findViewById(R.id.footer_img);
        no_zones = findViewById(R.id.no_zones);
        humidity = findViewById(R.id.humidity);
        temperature = findViewById(R.id.temperature);

        // empty bucket card
        dash_warn_waterfill_card.setVisibility(View.GONE);

        // auto switch
        autoSwitch.setChecked(session.getSwitchStatus());

        // weather from session
        mTemperature = session.getTemp();
        mHumidity = session.getHumidity();

        Log.d(TAG, "CITY: " + city);
        double celsius = mTemperature - 273.15;
        String formatted_celsius = String.format("%.1f", celsius);
        humidity.setText(mHumidity + "%");
        temperature.setText(formatted_celsius + "°C");

        // Actions
        changeAutoOrManual();
        getUserHub();

        Handler handler = new Handler();
        Runnable runnable = this::getLocation;
        handler.postDelayed(runnable, 10000);

        Handler handler2 = new Handler();

        // Request Hub Call every 3 seconds
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                dashBoardViewModel.getHubByUser(getApplicationContext());

                Runnable runnable2 = () -> weatherCall();
                // Request Weather Call every 15 seconds
                handler2.postDelayed(runnable2, 15000);
            }

        };

        Timer timer = new Timer();
        long delay = 0;
        long intervalPeriod = 3000;
        timer.scheduleAtFixedRate(task, delay,
                intervalPeriod);
    }

    /**
     * @author: Albert Kaminski
     * get current Location
     */
    private void getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        if (locationManager != null) {
            location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        session.saveLocation(longitude, latitude);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.logoutUser();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    /**
     * MVVM zoneDataViewModel sends Hub Data.
     * Sets the hubID globally
     * and checks if Bucket is empty.
     * Updates Hub when setting is changed to
     * Manual or Automatic watering.
     */

    private void getUserHub() {
        dashBoardViewModel.getHubDataObserver().observe(this, hubModel -> {
            if (hubModel != null) {

                hub_Id = hubModel.getHubId();
                // bucket empty
                if (hubModel.getBucketEmpty() && hubModel.getBucketEmpty() != null) {
                    dash_warn_waterfill_card.setVisibility(View.VISIBLE);
                } else {
                    dash_warn_waterfill_card.setVisibility(View.GONE);
                }

                // hub: auto or manual
                switchStatus = hubModel.getAuto();
                session.setSwitchStatus(switchStatus);
                if (hubModel.getAuto() != null) {
                    autoSwitch.setChecked(hubModel.getAuto());
                } else {
                    autoSwitch.setChecked(session.getSwitchStatus());
                }

                if (hubModel.getAuto() != null) {
                    updateAuto(hubModel.getAuto());
                } else {
                    updateAuto(session.getSwitchStatus());
                }

                // after hub data is fetched, get all Zones for this hub
                getZoneObserverData();
            }
        });

    }


    /**
     * OnCheckedListener for Switch
     * when Switch is true, is automatic
     * if false, then manual
     */
    private void changeAutoOrManual() {

        autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // API CALL to update hub on server
            updateAuto(isChecked);
            switchStatus = isChecked;
            session.setSwitchStatus(isChecked);

            // change UI
            changeUIAuto(isChecked);
        });
    }


    /**
     * update hub on server when switch is changed
     */
    private void updateAuto(boolean isChecked) {
        dashBoardViewModel.getHubUpdateDataObserver().observe(this, hubModel -> {
            if (hubModel != null) {
                switchStatus = hubModel.getAuto();
            }
            session.setSwitchStatus(switchStatus);
            changeUIAuto(session.getSwitchStatus());
        });
        dashBoardViewModel.updateAuto(this, hub_Id, isChecked);
    }


    /**
     * get zones of an hub
     */
    private void getZoneObserverData() {

        // recyclerView for zones
        recyclerView = findViewById(R.id.recyclerViewDashboard);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        // add layoutManager to recyclerView
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ZoneRecyclerAdapter(this, this, switchStatus);
        recyclerView.setAdapter(adapter);

        // recyclerView for dry zone warnings
        dRecyclerView = findViewById(R.id.recyclerViewDashboardDry);
        dLayoutManager = new LinearLayoutManager(this);
        dLayoutManager.setOrientation(RecyclerView.VERTICAL);

        // add layoutManager der recyclerView
        dRecyclerView.setLayoutManager(dLayoutManager);
        dAdapter = new DryZoneRecyclerAdapter(this, position -> {
            int zone_id = zoneDataList.get(position).getZoneId();
            Intent intent = new Intent(getApplicationContext(), ZoneDetailActivity.class);
            intent.putExtra("auto_or_manual", switchStatus);
            intent.putExtra("zone_id", zone_id);
            intent.putExtra("hub_id", hub_Id);
            startActivity(intent);
        });
        dRecyclerView.setAdapter(dAdapter);

        // observe zoneData coming in async
        dashBoardViewModel.getZoneDataObserver().observe(this, zoneModels -> {
            if (zoneModels != null) {
                zoneDataList = zoneModels;
                List<ZoneModel> distinctZoneDataList = zoneModels.stream().distinct().collect(Collectors.toList());

                zoneCount = zoneDataList.size();
                if (zoneDataList.size() == 0) {
                    no_zones.setVisibility(View.VISIBLE);
                } else {
                    no_zones.setVisibility(View.GONE);
                }
                adapter.setData(distinctZoneDataList);

                addButtonorNot(zoneCount);

                List<ZoneModel> dryZones;

                if (!switchStatus) {
                    for (ZoneModel d : zoneDataList) {
                        if (d.getIsDry()) {
                            dryZones = new ArrayList<>();
                            dryZones.add(d);
                            dAdapter.setData(dryZones);
                        }
                    }
                }


            }
        });

        // make the API Calls
        dashBoardViewModel.getAllZoneByHub(this, hub_Id);

        if (zoneCount == 0) {
            no_zones.setVisibility(View.VISIBLE);
        } else {
            no_zones.setVisibility(View.GONE);
        }
    }

    /**
     * Inits when UI is changed (auto)
     */
    private void changeUIAuto(Boolean auto) {

        if (auto) {
            autowater_card.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_autowater));
            autowater_message.setText(getResources().getText(R.string.dash_auto_water_title));
            autowater_text.setText(getResources().getText(R.string.dash_auto_water_text));
            footer_img.setImageResource(R.drawable.visual_footer_dashboard_auto);

            addButton.setColorFilter(ContextCompat.getColor(this, R.color.dark_blue));

            if (recyclerView != null) {
                for (int i = 0; i < zoneCount; i++) {
                    Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).getChildAt(i)).findViewById(R.id.dash_zone_item_container).setBackground(ContextCompat.getDrawable(this, R.drawable.autowater));
                }
            }
        } else {
            autowater_card.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_autowater));
            autowater_message.setText(getResources().getText(R.string.dash_manual_watering_title));
            autowater_text.setText(getResources().getText(R.string.dash_manual_watering_text));
            footer_img.setImageResource(R.drawable.visual_footer_dashboard_manual);
            addButton.setColorFilter(ContextCompat.getColor(this, R.color.dark_green));

            if (recyclerView != null) {
                for (int i = 0; i < zoneCount; i++) {
                    Objects.requireNonNull(Objects.requireNonNull(recyclerView.getLayoutManager()).getChildAt(i)).findViewById(R.id.dash_zone_item_container).setBackground(ContextCompat.getDrawable(this, R.drawable.manualwater));
                }
            }
        }
    }

    /**
     * @author: Albert Kaminski
     * Weather Call
     * Get Temperature and Humidity of Location that is set in the settings
     */

    private void weatherCall() {
        Retrofit retrofit = WeatherClient.getInstance();

        // Instance of retrofit with BloomCall
        WeatherService weatherCall = retrofit.create(WeatherService.class);
        String api_key = "8e2b1a39043fbcaaa31e31e2d6b584e6";
        Call<WeatherModel> call = weatherCall.getWeatherData(latitude, longitude, api_key);

        call.enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {

                // put the response.body in zone
                if (response.isSuccessful()) {

                    assert response.body() != null;
                    session.saveWeather(response.body());

                    mTemperature = session.getTemp();
                    mHumidity = session.getHumidity();

                    Log.d(TAG, "sTemperature" + mTemperature);

                    double celsius = mTemperature - 273.15;
                    String formatted_celsius = String.format("%.1f", celsius);
                    humidity.setText(mHumidity + "%");
                    temperature.setText(formatted_celsius + "°C");
                } else {
                    Log.d(TAG, "onResponse, but still failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                Log.d(TAG, "Error: " + t.getMessage());
            }
        });
    }

    // if more then 3 Zones, Remove the Add Button
    private void addButtonorNot(int zoneCount) {
        if (zoneCount > 3) {
            addButton.setVisibility(View.GONE);
        }
    }

    // onClickListener for recyclerView viewHolders
    @Override
    public void onItemClick(int position) {
        int zone_id = zoneDataList.get(position).getZoneId();
        Log.d(TAG, "onItemClick: " + zoneDataList.get(position).getZoneId());
        //Toast.makeText(this, "" + zoneDataList.get(position).getZoneId(), Toast.LENGTH_SHORT).show();

        //go To ZoneDetail
        Intent intent = new Intent(this, ZoneDetailActivity.class);
        intent.putExtra("auto_or_manual", switchStatus);
        intent.putExtra("zone_id", zone_id);
        intent.putExtra("hub_id", hub_Id);
        startActivity(intent);
    }
}