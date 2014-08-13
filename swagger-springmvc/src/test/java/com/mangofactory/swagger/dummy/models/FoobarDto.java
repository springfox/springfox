package com.mangofactory.swagger.dummy.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class FoobarDto {

  @JsonIgnore
  private final Foobar foobar;

  @JsonIgnore
  private Long visibleForSerialize;

  @JsonProperty
  @JsonInclude
  public Long getVisibleForSerialize() {
    return visibleForSerialize;
  }

  @JsonIgnore
  public void setVisibleForSerialize(Long visibleForSerialize) {
    this.visibleForSerialize = visibleForSerialize;
  }

  @JsonProperty("foobar")
  public String getFoobarCode() {
    return foobar == null ? null : foobar.getDisplayName();
  }

  public Foobar getFoobar() {
    return foobar;
  }

  public FoobarDto(Foobar foobar) {
    this.foobar = foobar;
  }

  public enum Foobar {
    FOO(0, "Foo"),
    BAR(1, "Bar")
    ;
    private final int value;

    @JsonCreator
    public static Foobar lookup(Integer value) {
      if(value == null) {
        return null;
      }
      for (Foobar obj : values()) {
        if (obj.value == value) {
          return obj;
        }
      }
      return null;
    }

    private final String displayName;

    @JsonValue
    public int getValue() {
      return value;
    }

    public String getDisplayName() {
      return displayName;
    }

    private Foobar(int value, String displayName) {
      this.value = value;
      this.displayName = displayName;
    }
  }
}
