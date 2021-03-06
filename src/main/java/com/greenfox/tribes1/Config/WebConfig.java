package com.greenfox.tribes1.Config;

import com.greenfox.tribes1.Logging.LoggerInterceptor;
import com.greenfox.tribes1.Logging.UpdateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  private List<String> excludeEndpoints = Arrays.asList("/register", "/login");

  @Autowired
  public void setUpdateInterceptor(UpdateInterceptor updateInterceptor) {
    this.updateInterceptor = updateInterceptor;
  }

  UpdateInterceptor updateInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoggerInterceptor());
    registry.addInterceptor(updateInterceptor).excludePathPatterns(excludeEndpoints);
    //registry.addInterceptor(new ProgressionInterceptor()).excludePathPatterns(excludeEndpoints);
  }

  /*@Bean
  UpdateInterceptor updateInterceptor() {
    return new UpdateInterceptor();
  }*/
}
