package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllowableRangeValues implements AllowableValues {
  @JsonProperty("minimum")
  private final String min;
  @JsonProperty("maximum")
  private final String max;

  public AllowableRangeValues(String min, String max) {
    this.min = min;
    this.max = max;
  }

  public String getMin() {
    return min;
  }

  public String getMax() {
    return max;
  }
}
