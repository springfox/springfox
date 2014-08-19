package com.mangofactory.swagger.readers.operation.parameter;

import com.wordnik.swagger.model.Parameter;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mangofactory.swagger.models.Types.*;
import static java.lang.reflect.Modifier.*;

class ModelAttributeParameterExpander {

  public void expand(final String parentName, final Class<?> paramType,
                     final List<Parameter> parameters) {

    Set<String> beanPropNames = getBeanPropertyNames(paramType);
    List<Field> fields = getAllFields(paramType);

    for (Field field : fields) {
      if (isStatic(field.getModifiers()) || field.isSynthetic() || !beanPropNames.contains(field.getName())) {
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

  private List<Field> getAllFields(final Class<?> type) {

    List<Field> result = new ArrayList<Field>();

    Class<?> i = type;
    while (i != null && i != Object.class) {
      result.addAll(Arrays.asList(i.getDeclaredFields()));
      i = i.getSuperclass();
    }

    return result;
  }

  private Set<String> getBeanPropertyNames(final Class<?> clazz) {

    try {
      Set<String> beanProps = new HashSet<String>();
      PropertyDescriptor[] propDescriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();

      for (int i = 0; i < propDescriptors.length; i++) {

        if (propDescriptors[i].getReadMethod() != null && propDescriptors[i].getWriteMethod() != null) {
          beanProps.add(propDescriptors[i].getName());
        }
      }

      return beanProps;

    } catch (IntrospectionException e) {
      throw new RuntimeException(new StringBuilder("Failed to get bean properties on ").append(clazz).toString(), e);
    }

  }

}
