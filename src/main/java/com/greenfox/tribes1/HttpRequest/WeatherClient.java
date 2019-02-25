package com.greenfox.tribes1.HttpRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
@Component
public class WeatherClient {
  private static final String BASE_URL ="http://api.openweathermap.org/data/2.5/";
  private WeatherService weatherService;

  public WeatherClient() {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    weatherService = retrofit.create(WeatherService.class);
  }

  public WeatherAPI getWeatherAPI() throws IOException {
    Call<WeatherAPI> callSync = weatherService.getWeatherAPI("Budapest", "351c1d44876722af5e62151d4572c454", "metric");

    Response<WeatherAPI> response = callSync.execute();
    WeatherAPI weatherAPI = response.body();
    return weatherAPI;
  }


}
