package com.greenfox.tribes1.HttpRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class GithubController {
  private Client client;
  private WeatherClient weatherClient;

  @Autowired
  public GithubController(Client client, WeatherClient weatherClient) {
    this.client = client;
    this.weatherClient = weatherClient;
  }

  @GetMapping("/github")
  public ResponseEntity getGithubAccount() {
    try {
      User user = client.getUser();
      return ResponseEntity.ok(user);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ResponseEntity.badRequest().build();
  }

  @GetMapping("/weather")
  public ResponseEntity getWeather() {
    try {
      WeatherAPI weatherAPI = weatherClient.getWeatherAPI();
      return ResponseEntity.ok(weatherAPI);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ResponseEntity.badRequest().build();
  }
}
