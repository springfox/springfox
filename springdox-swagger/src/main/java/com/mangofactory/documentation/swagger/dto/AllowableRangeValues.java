package com.mangofactory.documentation.swagger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AllowableRangeValues implements AllowableValues {
  @JsonProperty("minimum")
  private String min;
  @JsonProperty("maximum")
  private String max;

  public AllowableRangeValues() {
  }

  public AllowableRangeValues(String min, String max) {
    this.min = min;
    this.max = max;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }
}
