package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeWithJsonProperty {
  @JsonProperty("some_odd_ball_name")
  private String someOddBallName;

  public TypeWithJsonProperty(String someOddBallName) {
    this.someOddBallName = someOddBallName;
  }

  public String getSomeOddBallName() {
    return someOddBallName;
  }

  public void setSomeOddBallName(String someOddBallName) {
    this.someOddBallName = someOddBallName;
  }
}
