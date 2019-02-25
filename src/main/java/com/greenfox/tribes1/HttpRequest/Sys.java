package com.greenfox.tribes1.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import retrofit2.http.GET;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "id",
        "message",
        "country",
        "sunrise",
        "sunset"
})
@Getter
@Setter
public class Sys {

  @JsonProperty("type")
  private Integer type;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("message")
  private Double message;
  @JsonProperty("country")
  private String country;
  @JsonProperty("sunrise")
  private Integer sunrise;
  @JsonProperty("sunset")
  private Integer sunset;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}