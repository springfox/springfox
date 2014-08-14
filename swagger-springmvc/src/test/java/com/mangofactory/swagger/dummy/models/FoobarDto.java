package com.mangofactory.swagger.dummy.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    return foobar == null ? null : foobar.name();
  }

  public Foobar getFoobar() {
    return foobar;
  }

  public FoobarDto(@JsonProperty("foobar") String foobar) {
    this.foobar = Foobar.valueOf(foobar);
  }

  public enum Foobar {
    FOO,
    BAR
  }
}
