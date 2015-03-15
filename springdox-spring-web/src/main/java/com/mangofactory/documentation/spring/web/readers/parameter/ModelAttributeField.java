package com.mangofactory.documentation.spring.web.readers.parameter;

import java.lang.reflect.Field;

public class ModelAttributeField {
  private final Class<?> fieldType;
  private final Field field;

  public ModelAttributeField(Class<?> fieldType, Field field) {
    this.fieldType = fieldType;
    this.field = field;
  }

  public Class<?> getFieldType() {
    return fieldType;
  }

  public Field getField() {
    return field;
  }
}
