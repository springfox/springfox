package com.mangofactory.swagger.models;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class Types {

    private static final String DATE_TYPE = "date";
    private static final String DATE_TIME_TYPE = "date-time";

    private static final Set<String> baseTypes
            = newHashSet("int", DATE_TYPE, DATE_TIME_TYPE, "string", "double", "float", "boolean", "byte", "object", "long");
    private static final Map<Type, String> typeNameLookup = ImmutableMap.<Type, String>builder()
            .put(Long.TYPE, "long")
            .put(Short.TYPE, "int")
            .put(Integer.TYPE, "int")
            .put(Double.TYPE, "double")
            .put(Float.TYPE, "float")
            .put(Byte.TYPE, "byte")
            .put(Boolean.TYPE, "boolean")
            .put(Character.TYPE, "string")

            .put(Date.class, DATE_TIME_TYPE)
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
            .build();

    public static String typeNameFor(final Type type) {

        String typeName = typeNameLookup.get(type);

        if (typeName == null) {

            //we can't return the class names without the space or Swagger UI will show a
            //complex type for LocalDate instead of just a String
            if (type.getTypeName().endsWith("LocalDate")) {
                typeName = "date ";
            } else if (type.getTypeName().endsWith("LocalDateTime")) {
                typeName = "date-time ";
            }
        }

        return typeName;
    }

    public static boolean isBaseType(final String typeName) {
        return baseTypes.contains(typeName);
    }


}
