package com.greenfox.tribes1.HttpRequest;

import lombok.Getter;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

@Getter
@Component
public class Client {

  private static final String BASE_URL = "https://api.github.com/";
  private UserService userService;

  public Client() {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    userService = retrofit.create(UserService.class);
  }

  public User getUser() throws IOException {
    Call<User> callSync = userService.getUser("bleaksmile");

    Response<User> response = callSync.execute();
    User user = response.body();
    return user;
  }
}
