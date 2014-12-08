package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AllowableListValues implements AllowableValues {
  @JsonProperty("enum")
  private final List<String> values;
  @JsonIgnore
  private final String valueType;

  public AllowableListValues(List<String> values, String valueType) {
    this.values = values;
    this.valueType = valueType;
  }

  public List<String> getValues() {
    return values;
  }

  public String getValueType() {
    return valueType;
  }
}
