package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.RefProperty;

public class DefaultPropertyFactory  implements Function<ModelProperty, Property> {
  @Override
  public Property apply(ModelProperty input) {
    return new RefProperty(input.getName());
  }
}
