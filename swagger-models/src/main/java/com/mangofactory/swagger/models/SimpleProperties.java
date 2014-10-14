package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.swagger.models.property.BooleanFactory;
import com.mangofactory.swagger.models.property.DateFactory;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

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

  public static Optional<Property> from(ModelProperty property) {
    if (propertyFactory.get(property.qualifiedTypeName()) == null) {
      return Optional.absent();
    }
    return Optional.fromNullable(propertyFactory.get(property.qualifiedTypeName()).apply(property));
  }

  public static Property fromType(ResolvedType property) {
    Property defaultProperty = new ObjectProperty();
    if (IsInt32(property)) {
      return new IntegerProperty();
    } else if (isFloat(property)) {
      return new FloatProperty();
    } else if (isDouble(property)) {
      return new DoubleProperty();
    } else if (isLong(property)) {
      return new LongProperty();
    } else if (isBoolean(property)) {
      return new BooleanProperty();
    } else if (isString(property)) {
      return new StringProperty()._enum(allowableValues(property).or(new ArrayList<String>()));
    } else if (property.getErasedType().equals(Date.class)) {
      return new DateProperty();
    }
    return defaultProperty;
  }

  private static boolean isString(ResolvedType property) {
    return property.getErasedType().equals(String.class)
            || property.getErasedType().equals(Byte.class)
            || property.getErasedType().isEnum();
  }

  private static boolean isBoolean(ResolvedType property) {
    return property.getErasedType().equals(Boolean.class)
            || property.getErasedType().equals(Boolean.TYPE);
  }

  private static boolean isLong(ResolvedType property) {
    return property.getErasedType().equals(Long.class) || property.getErasedType().equals(Long.TYPE)
            || property.getErasedType().equals(BigInteger.class);
  }

  private static boolean isDouble(ResolvedType property) {
    return property.getErasedType().equals(Double.class)
            || property.getErasedType().equals(BigDecimal.class)
            || property.getErasedType().equals(Double.TYPE);
  }

  private static boolean isFloat(ResolvedType property) {
    return property.getErasedType().equals(Float.class)
            || property.getErasedType().equals(Float.TYPE);
  }

  private static boolean IsInt32(ResolvedType property) {
    return property.getErasedType().equals(Integer.class)
            || property.getErasedType().equals(Short.class)
            || property.getErasedType().equals(Integer.TYPE)
            || property.getErasedType().equals(Short.TYPE);
  }
}
