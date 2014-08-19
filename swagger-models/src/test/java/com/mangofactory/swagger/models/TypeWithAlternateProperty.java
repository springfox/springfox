package com.mangofactory.swagger.models;

import org.joda.time.LocalDate;

public class TypeWithAlternateProperty {
  private LocalDate localDate;

  public TypeWithAlternateProperty(LocalDate localDate) {
    this.localDate = localDate;
  }

  public LocalDate getLocalDate() {
    return localDate;
  }

  public void setLocalDate(LocalDate localDate) {
    this.localDate = localDate;
  }
}
