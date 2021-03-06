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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"lon",
"lat"
})
@Getter
@Setter
public class Coord {

@JsonProperty("lon")
private Double lon;
@JsonProperty("lat")
private Double lat;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}