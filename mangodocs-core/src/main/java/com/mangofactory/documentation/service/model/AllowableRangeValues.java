package com.mangofactory.documentation.service.model;

public class AllowableRangeValues implements AllowableValues {
  private final String min;
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
