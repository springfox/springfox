package com.mangofactory.springmvc.plugins;

import com.google.common.base.Function;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.service.model.AllowableListValues;
import com.mangofactory.service.model.AllowableValues;
import com.mangofactory.service.model.builder.ParameterBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.*;

public class ParameterExpansionContext {

  private final String dataTypeName;
  private final String parentName;
  private final Field field;
  private final DocumentationType documentationType;
  private ParameterBuilder parameterBuilder;

  public ParameterExpansionContext(String dataTypeName, String parentName, Field field, DocumentationType
          documentationType) {
    this.dataTypeName = dataTypeName;
    this.parentName = parentName;
    this.field = field;
    this.documentationType = documentationType;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public String getParentName() {
    return parentName;
  }

  public Field getField() {
    return field;
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public ParameterBuilder getParameterBuilder() {
    return parameterBuilder;
  }


}
