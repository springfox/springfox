package com.mangofactory.swagger.readers.operation.parameter;

import com.wordnik.swagger.model.Parameter;

import java.lang.reflect.Field;
import java.util.List;

import static com.mangofactory.swagger.models.Types.*;
import static java.lang.reflect.Modifier.*;

class ModelAttributeParameterExpander {

  public void expand(final String parentName, final Class<?> paramType,
                     final List<Parameter> parameters) {

    Field[] fields = paramType.getDeclaredFields();

    for (Field field : fields) {
      if (isStatic(field.getModifiers()) || field.isSynthetic()) {
        continue;
      }

      if (!typeBelongsToJavaPackage(field) && !field.getType().isEnum()) {

        expand(field.getName(), field.getType(), parameters);
        continue;
      }

      String dataTypeName = typeNameFor(field.getType());

      if (dataTypeName == null) {
        dataTypeName = field.getType().getSimpleName();
      }

      parameters.add(new ParameterBuilder()
              .forField(field)
              .withDataTypeName(dataTypeName)
              .withParentName(parentName)
              .build());

    }
  }

  private boolean typeBelongsToJavaPackage(Field field) {
    return (field.getType().getPackage() == null || field.getType().getPackage().getName().startsWith("java"));
  }

}
