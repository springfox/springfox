package com.mangofactory.swagger.models.property;

import com.google.common.base.Function;
import com.wordnik.swagger.models.properties.DoubleProperty;
import com.wordnik.swagger.models.properties.FloatProperty;
import com.wordnik.swagger.models.properties.IntegerProperty;
import com.wordnik.swagger.models.properties.LongProperty;
import com.wordnik.swagger.models.properties.Property;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumericFactory implements Function<ModelProperty, Property> {
  @Override
  public Property apply(ModelProperty input) {
    if (input.qualifiedTypeName().equals(Integer.class.getName())
        || input.qualifiedTypeName().equals(Integer.TYPE.getName())
        || input.qualifiedTypeName().equals(Short.class.getName())
        || input.qualifiedTypeName().equals(Short.TYPE.getName())) {
      return new IntegerProperty();
    } else if (input.qualifiedTypeName().equals(Float.class.getName())
            || input.qualifiedTypeName().equals(Float.TYPE.getName())) {
      return new FloatProperty();
    } else if (input.qualifiedTypeName().equals(Double.class.getName())
            || input.qualifiedTypeName().equals(Double.TYPE.getName())
            || input.qualifiedTypeName().equals(BigDecimal.class.getName())) {
      return new DoubleProperty();
    } else if (input.qualifiedTypeName().equals(Long.class.getName())
            || input.qualifiedTypeName().equals(Long.TYPE.getName())
            || input.qualifiedTypeName().equals(BigInteger.class.getName())) {
      return new LongProperty();
    }
    throw new IllegalArgumentException("Unknown type");
  }
}
