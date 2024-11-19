package com.example.test_project.api;

import com.example.test_project.model.HubModel;
import com.example.test_project.model.SensorModel;
import com.example.test_project.model.UserModel;
import com.example.test_project.model.ZoneModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author: Albert Kaminski
 * Call Interface for HTTPS Requests (from the Webserver) to use in RetrofitClient
 **/
public interface BloomCall {

    /**
     * Hub
     */

    @GET("hub/getHubByUser/")
    Call<HubModel> getHubByUser();

    @Headers({"Content-Type: application/json"})
    @PUT("hub/updateHub/{hub_id}")
    Call<HubModel> updateHub(
            @Path("hub_id") Integer hubId,
            @Body HubModel newHub);

    /**
     * Sensor
     */

    @GET("sensor/getSensor")
    Call<SensorModel> getSensor(@Query("hub_id") Integer hubId,
                                @Query("zone_id") Integer zoneId);

    @DELETE("sensor/deleteSensor")
    Call<SensorModel> deleteSensor(
            @Query("hub_id") Integer hubId,
            @Query("zone_id") Integer zoneId);

    /**
     * UserLogin
     */

    @POST("user/login")
    Call<Void> login(@Header("Authorization") String basicAuth);

    /**
     * User
     */

    @Headers({"Content-Type: application/json"})
    @POST("user/register")
    Call<UserModel> register(@Body UserModel user);

    /**
     * Zone
     */

    @GET("zone/getAllZonesbyHubId/{hub_id}")
    Call<List<ZoneModel>> getAllZonesbyHub(@Path("hub_id") Integer hubId);

    @GET("zone/getZone")
    Call<ZoneModel> getZone(@Query("hub_id") Integer hubId,
                            @Query("zone_id") Integer zoneId);

    @Headers({"Content-Type: application/json"})
    @PUT("zone/updateZone")
    Call<ZoneModel> updateZone(
            @Query("hub_id") Integer hubId,
            @Query("zone_id") Integer zoneId,
            @Body ZoneModel newZone);

    @DELETE("zone/deleteZone")
    Call<ZoneModel> deleteZone(@Query("hub_id") Integer hubId,
                               @Query("zone_id") Integer zoneId);

}
