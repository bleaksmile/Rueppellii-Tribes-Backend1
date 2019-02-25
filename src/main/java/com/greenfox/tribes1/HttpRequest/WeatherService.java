package com.greenfox.tribes1.HttpRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

  @GET("weather?")
  public Call<WeatherAPI> getWeatherAPI(@Query("q") String q, @Query("APPID") String APPID, @Query("units") String units);
}
