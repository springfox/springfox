package com.mangofactory.swagger.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.ANY,
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
public class TypeWithJsonGetterAnnotation {
  private String value1;

  @JsonCreator
  public TypeWithJsonGetterAnnotation(@JsonProperty("value1") final String value1) {
    this.value1 = value1;
  }

  @JsonGetter("value1")
  public String value1() {
    return value1;
  }
}
