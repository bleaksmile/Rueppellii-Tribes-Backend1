package com.greenfox.tribes1.HttpRequest;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"all"
})
@Getter
@Setter
public class Clouds {

@JsonProperty("all")
private Integer all;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}