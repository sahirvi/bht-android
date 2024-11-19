package com.example.test_project.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test_project.api.BloomCall;
import com.example.test_project.model.HubModel;
import com.example.test_project.model.ZoneModel;
import com.example.test_project.viewHelper.AuthInterceptor;
import com.example.test_project.viewHelper.UnsafeOkHttpClient;
import com.example.test_project.viewHelper.UserSessionManager;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: Sahiram Ravikumar
 * ViewModel for Dashboard to get an hub by user, update auto status and get all zones by an hub
 */
public class DashBoardViewModel extends ViewModel {

    private BloomCall bloomCall;
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private UserSessionManager session;
    private MutableLiveData<List<ZoneModel>> zoneData;
    private MutableLiveData<HubModel> hubData;
    private MutableLiveData<HubModel> hubUpdateData;

    private static final String TAG = "ZoneDataViewModel";

    /**
     * Constructor
     */
    public DashBoardViewModel() {
        super();
        zoneData = new MutableLiveData<>();
        hubData = new MutableLiveData<>();
        hubUpdateData = new MutableLiveData<>();
    }

    public MutableLiveData<List<ZoneModel>> getZoneDataObserver() {
        return zoneData;
    }

    public MutableLiveData<HubModel> getHubDataObserver() {
        return hubData;
    }

    public MutableLiveData<HubModel> getHubUpdateDataObserver() {
        return hubUpdateData;
    }

    /**
     * Updates auto with hub_id (to change auto or manual watering with switch)
     * Sends HubModel(auto) as body and hub_idas query parameter.
     */
    public void updateAuto(Context context, Integer hub_id, boolean isChecked) {
        retrofitCall(context);

        HubModel hubModel = new HubModel(isChecked);

        // asynchronous PUT request of an hub by hub_id
        Call<HubModel> updateAutoHub = bloomCall.updateHub(hub_id, hubModel);
        updateAutoHub.enqueue(new Callback<HubModel>() {
            @Override
            public void onResponse(@NonNull Call<HubModel> call, @NonNull Response<HubModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in updated hub
                if (response.isSuccessful()) {
                    hubUpdateData.postValue(response.body());
                } else {
                    hubUpdateData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HubModel> call, @NonNull Throwable t) {
                hubUpdateData.postValue(null);
            }
        });
    }

    /**
     * Gets an hub by an user
     * Sends token and gets hub in response body
     */
    public void getHubByUser(Context context) {

        retrofitCall(context);

        Call<HubModel> callHub = bloomCall.getHubByUser();

        // asynchronous GET request of an hub by token
        callHub.enqueue(new Callback<HubModel>() {
            @Override
            public void onResponse(@NonNull Call<HubModel> call, @NonNull Response<HubModel> response) {
                // check if new header because of refresh token
                if (response.headers().get("Authorization") != null) {
                    session = new UserSessionManager(context);
                    session.logoutUser();
                    session.createUserLoginSession(response.headers().get("Authorization"));
                }
                // put the response.body in hub
                if (response.isSuccessful()) {
                    hubData.postValue(response.body());
                } else {
                    hubData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HubModel> call, @NonNull Throwable t) {
                hubData.postValue(null);
            }
        });
    }

    /**
     * Gets all zones of an hub
     * Sends hub id and gets all zones in response body
     */
    public void getAllZoneByHub(Context context, int hubId) {
        retrofitCall(context);

        Call<List<ZoneModel>> callHub = bloomCall.getAllZonesbyHub(hubId);

        // asynchronous GET request of all zones of an hub by hub_id
        callHub.enqueue(new Callback<List<ZoneModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ZoneModel>> call, @NonNull Response<List<ZoneModel>> response) {
                if (response.isSuccessful()) {
                    // check if new header because of refresh token
                    if (response.headers().get("Authorization") != null) {
                        session = new UserSessionManager(context);
                        Log.d(TAG, "VM NEW HEADER zone: " + response.headers().get("Authorization"));
                        session.logoutUser();
                        session.createUserLoginSession(response.headers().get("Authorization"));
                    }
                    // put the response.body in sensor
                    zoneData.postValue(response.body());
                } else {
                    zoneData.postValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ZoneModel>> call, @NonNull Throwable t) {
                zoneData.postValue(null);
                Log.d(TAG, "onFailure list of zones: " + t.getMessage());
            }
        });
    }

    /**
     * Retrofit call to use BloomCall with updatedClient
     */
    private void retrofitCall(Context context) {
        //OkHttp
        okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        // to send request with token
        OkHttpClient updatedClient = okHttpClient
                .newBuilder()
                .addInterceptor(new AuthInterceptor(context))
                .build();

        // Client to make requests to Server-URL
        retrofit = new Retrofit.Builder()
                .baseUrl("https://217.160.45.110:443/")
                .client(updatedClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Instance of retrofit with BloomCall

        bloomCall = retrofit.create(BloomCall.class);
    }
}