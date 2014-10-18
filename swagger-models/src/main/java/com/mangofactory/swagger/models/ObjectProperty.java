package com.mangofactory.swagger.models;

import com.wordnik.swagger.models.properties.AbstractProperty;
import com.wordnik.swagger.models.properties.Property;

public class ObjectProperty extends AbstractProperty implements Property {
  public ObjectProperty() {
    setType("object");
  }
}
