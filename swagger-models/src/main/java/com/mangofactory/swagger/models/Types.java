package com.mangofactory.swagger.models;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.*;

public class Types {
  private static final Set<String> baseTypes
          = newHashSet("int", "date", "string", "double", "float", "boolean", "byte", "object", "long");
  private static final Map<Type, String> typeNameLookup = ImmutableMap.<Type, String>builder()
          .put(Long.TYPE, "long")
          .put(Short.TYPE, "int")
          .put(Integer.TYPE, "int")
          .put(Double.TYPE, "double")
          .put(Float.TYPE, "float")
          .put(Byte.TYPE, "byte")
          .put(Boolean.TYPE, "boolean")
          .put(Character.TYPE, "string")

          .put(Date.class, "date-time")
          .put(String.class, "string")
          .put(Object.class, "object")
          .put(Long.class, "long")
          .put(Integer.class, "int")
          .put(Short.class, "int")
          .put(Double.class, "double")
          .put(Float.class, "float")
          .put(Boolean.class, "boolean")
          .put(Byte.class, "byte")
          .put(BigDecimal.class, "double")
          .put(BigInteger.class, "long")
          .put(Currency.class, "string")
          .build();

  public static String typeNameFor(Type type) {
    return typeNameLookup.get(type);
  }

  public static boolean isBaseType(String typeName) {
    return baseTypes.contains(typeName);
  }


}
