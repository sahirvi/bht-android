package com.example.test_project.api;

import com.example.test_project.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author: Albert Kaminski
 * Interface that defines possible weather HTTP operations
 *
 * API KEY: 8e2b1a39043fbcaaa31e31e2d6b584e6
 * api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
 **/
public interface WeatherService {

    @GET("data/2.5/weather?")
    Call<WeatherModel> getWeatherData(@Query("lat") double lat, @Query("lon") double lon, @Query("APPID") String app_id);
}
