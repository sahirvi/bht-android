package com.example.gps_tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView title;
    TextView latlng;
    TextView altitude;
    TextView speed;

    ImageView route;
    boolean registeredLocationManager = false;
    LocationManager locationManager;
    FloatingActionButton actionButton;
    RecordHandler recordHandler;
    boolean init = false;
    List<Location> locationList;
    GPXHandler gpxHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gpxHandler = new GPXHandler();
        locationList = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        latlng = findViewById(R.id.latLng);
        altitude = findViewById(R.id.altitude);
        speed = findViewById(R.id.speed);
        title = findViewById(R.id.title);
        actionButton = findViewById(R.id.actionButton);
        route = findViewById(R.id.imageView_routeTracker);


        recordHandler = new RecordHandler(this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Log.d("MainActivity", "onLocationChanged");

        latlng.setText(String.format("%1$.3f °, %2$.3f °", location.getLatitude(), location.getLongitude()));

        altitude.setText(location.getAltitude() + " m");

        speed.setText(location.getSpeed() * 3.6f + " km/h");

        String time_stamp = new SimpleDateFormat("h:mm:ss a").format(new Date());
        gpxHandler.addDate(new Date());
        locationList.add(location);
        recordHandler.writeSample(time_stamp, location.getLongitude(), location.getLatitude(), location.getAltitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Falls ein Location Manager für Updates der Location bereits registriert wurde, wieder von Updates entfernen.
        if (registeredLocationManager) {
            locationManager.removeUpdates(this);
        } else {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }

        int id = item.getItemId();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        if (id == R.id.menuitem_gpsprovider) {
            Toast.makeText(getApplicationContext(), R.string.info_gps, Toast.LENGTH_SHORT).show();
            title.setText(R.string.gps_provider_title);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, this);
            registeredLocationManager = true;
        } else if (id == R.id.menuitem_networkprovider) {
            Toast.makeText(getApplicationContext(), R.string.info_network, Toast.LENGTH_SHORT).show();
            title.setText(R.string.network_provider_title);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 10, this);
            registeredLocationManager = true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void record(View view) {
        if (!init) {
            init = true;
            actionButton.setImageResource(R.drawable.ic_gps);
            recordHandler.record_start();
        } else {
            init = false;
            actionButton.setImageResource(R.drawable.ic_gps_off);
            recordHandler.record_stop();
            gpxHandler.writePath(locationList, this);

            ArrayList<String> longitudes = (ArrayList<String>) recordHandler.getLongitudes();
            ArrayList<String> latitudess = (ArrayList<String>) recordHandler.getLatitudes();
            Intent intent = new Intent(this, RouteActivity.class);
            intent.putStringArrayListExtra("longitudes", longitudes);
            intent.putStringArrayListExtra("latitudes", latitudess);
            startActivity(intent);
        }
    }
}
