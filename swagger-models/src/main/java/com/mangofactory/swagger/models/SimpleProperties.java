package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.swagger.models.property.BooleanFactory;
import com.mangofactory.swagger.models.property.DateFactory;
import com.mangofactory.swagger.models.property.DefaultPropertyFactory;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.NumericFactory;
import com.mangofactory.swagger.models.property.StringFactory;
import com.wordnik.swagger.models.properties.BooleanProperty;
import com.wordnik.swagger.models.properties.DateProperty;
import com.wordnik.swagger.models.properties.DoubleProperty;
import com.wordnik.swagger.models.properties.FloatProperty;
import com.wordnik.swagger.models.properties.IntegerProperty;
import com.wordnik.swagger.models.properties.LongProperty;
import com.wordnik.swagger.models.properties.Property;
import com.wordnik.swagger.models.properties.StringProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

public class SimpleProperties {

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
          .put(BigDecimal.class.getName(), new NumericFactory())
          .put(BigInteger.class.getName(), new NumericFactory())
          .put("int", new NumericFactory())
          .put("short", new NumericFactory())
          .put("long", new NumericFactory())
          .put("float", new NumericFactory())
          .put("double", new NumericFactory())
          .put("boolean", new BooleanFactory())
          .put("byte", new StringFactory())
          .build();

  public static Property from(ModelProperty property) {
    return Optional
            .fromNullable(propertyFactory.get(property.qualifiedTypeName()))
            .or(new DefaultPropertyFactory())
            .apply(property);

  }

  public static Property baseTypeFrom(ResolvedType property) {
    Property defaultProperty = new ObjectProperty();
    if (property.getErasedType().equals(Integer.class)
            || property.getErasedType().equals(Short.class)) {
      return new IntegerProperty();
    } else if (property.getErasedType().equals(Float.class)) {
      return new FloatProperty();
    } else if (property.getErasedType().equals(Double.class)
            || property.getErasedType().equals(BigDecimal.class)) {
      return new DoubleProperty();
    } else if (property.getErasedType().equals(Long.class)
            || property.getErasedType().equals(BigInteger.class)) {
      return new LongProperty();
    } else if (property.getErasedType().equals(Boolean.class)) {
      return new BooleanProperty();
    } else if (property.getErasedType().equals(String.class) || property.getErasedType().equals(Byte.class)) {
      return new StringProperty();
    } else if (property.getErasedType().equals(Date.class)) {
      return new DateProperty();
    }
    return defaultProperty;
  }
}
