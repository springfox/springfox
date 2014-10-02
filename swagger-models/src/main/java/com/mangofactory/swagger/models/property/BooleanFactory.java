package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.wordnik.swagger.models.properties.BooleanProperty;
import com.wordnik.swagger.models.properties.Property;

public class BooleanFactory implements Function<ModelProperty, Property> {
  @Override
  public Property apply(ModelProperty input) {
    return new BooleanProperty();
  }
}
