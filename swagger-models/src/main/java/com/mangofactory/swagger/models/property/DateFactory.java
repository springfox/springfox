package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.wordnik.swagger.models.properties.DateProperty;
import com.wordnik.swagger.models.properties.Property;

public class DateFactory implements Function<ModelProperty, Property> {
  @Override
  public Property apply(ModelProperty input) {
    DateProperty property = new DateProperty();
    property.setName(input.getName());
    return property;
  }
}
