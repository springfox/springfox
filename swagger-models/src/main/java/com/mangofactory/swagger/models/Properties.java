package com.mangofactory.swagger.models;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.swagger.models.property.BooleanFactory;
import com.mangofactory.swagger.models.property.DateFactory;
import com.mangofactory.swagger.models.property.DefaultPropertyFactory;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.NumericFactory;
import com.mangofactory.swagger.models.property.StringFactory;
import com.wordnik.swagger.models.properties.Property;

import java.util.Date;
import java.util.Map;

public class Properties {

  private static final Map<String, Function<ModelProperty, Property>> propertyFactory
          = ImmutableMap.<String, Function<ModelProperty, Property>>builder()
          .put(Date.class.getName(), new DateFactory())
          .put(Integer.class.getName(), new NumericFactory())
          .put(Long.class.getName(), new NumericFactory())
          .put(Short.class.getName(), new NumericFactory())
          .put(Double.class.getName(), new NumericFactory())
          .put(Float.class.getName(), new NumericFactory())
          .put(String.class.getName(), new StringFactory())
          .put(Boolean.class.getName(), new BooleanFactory())
          .put(Byte.class.getName(), new StringFactory())
          .put("int", new NumericFactory())
          .put("short", new NumericFactory())
          .put("long", new NumericFactory())
          .put("float", new NumericFactory())
          .put("double", new NumericFactory())
          .put("boolean", new BooleanFactory())
          .put("byte", new StringFactory())
          .build();

  public static Property from(ModelProperty property) {
    return Optional.fromNullable(propertyFactory.get(property.qualifiedTypeName()))
            .or(new DefaultPropertyFactory()).apply(property);
  }
}
