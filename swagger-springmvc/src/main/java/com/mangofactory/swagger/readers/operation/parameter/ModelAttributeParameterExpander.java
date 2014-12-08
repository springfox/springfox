package com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.dto.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mangofactory.swagger.models.ResolvedTypes.*;
import static com.mangofactory.swagger.models.Types.*;
import static java.lang.reflect.Modifier.*;

class ModelAttributeParameterExpander {
  private static final Logger LOGGER = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private AlternateTypeProvider alternateTypeProvider;
  private TypeResolver resolver = new TypeResolver();

  public ModelAttributeParameterExpander(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
  }

  public void expand(final String parentName, final Class<?> paramType,
                     final List<Parameter> parameters) {

    Set<String> beanPropNames = getBeanPropertyNames(paramType);
    List<Field> fields = getAllFields(paramType);
    LOGGER.debug("Expanding parameter type: {}", paramType);
    for (Field field : fields) {
      LOGGER.debug("Attempting to expanding field: {}", field);

      if (isStatic(field.getModifiers()) || field.isSynthetic() || !beanPropNames.contains(field.getName())) {
        LOGGER.debug("Skipping expansion of field: {}, not a valid bean property", field);
        continue;
      }
      Class<?> resolvedType = getResolvedType(field);
      if (!typeBelongsToJavaPackage(resolvedType) && !field.getType().isEnum()) {
        if (!field.getType().equals(paramType)) {
          LOGGER.debug("Expanding complex field: {} with type: {}", field, resolvedType);
          expand(field.getName(), field.getType(), parameters);
          continue;
        } else {
          LOGGER.warn("Skipping expanding complex field: {} with type: {} as it is recursively defined", field,
                  resolvedType);
        }
      }

      String dataTypeName = typeNameFor(resolvedType);

      if (dataTypeName == null) {
        dataTypeName = resolvedType.getSimpleName();
      }
      LOGGER.debug("Building parameter for field: {}, with type: ", field, resolvedType);
      parameters.add(new ParameterBuilder()
              .forField(field)
              .withDataTypeName(dataTypeName)
              .withParentName(parentName)
              .build());

    }
  }

  private Class<?> getResolvedType(Field field) {
    Class<?> type = field.getType();
    ResolvedType resolvedType = asResolved(resolver, type);
    ResolvedType alternativeType = alternateTypeProvider.alternateFor(resolvedType);
    Class<?> erasedType = alternativeType.getErasedType();
    if (type != erasedType) {
      LOGGER.debug("Found alternative type [{}] for field: [{}-{}]", erasedType, field, type);
    }
    return erasedType;
  }

  private boolean typeBelongsToJavaPackage(Class<?> type) {
    return type.getPackage() == null
            || type.getPackage().getName().startsWith("java")
            || Collection.class.isAssignableFrom(type)
            || Map.class.isAssignableFrom(type);
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
