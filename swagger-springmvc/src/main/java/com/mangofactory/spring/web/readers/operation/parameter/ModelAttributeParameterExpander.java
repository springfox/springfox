package com.mangofactory.spring.web.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.annotations.VisibleForTesting;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.service.model.builder.ParameterBuilder;
import com.mangofactory.spring.web.plugins.DocumentationPluginsManager;
import com.mangofactory.spring.web.plugins.ParameterExpansionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.BeanInfo;
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

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.schema.Types.*;
import static java.lang.reflect.Modifier.*;

@Component
public class ModelAttributeParameterExpander {
  private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private final AlternateTypeProvider alternateTypeProvider;
  private final TypeResolver resolver;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ModelAttributeParameterExpander(AlternateTypeProvider alternateTypeProvider, TypeResolver resolver,
                                         DocumentationPluginsManager pluginsManager) {
    this.alternateTypeProvider = alternateTypeProvider;
    this.resolver = resolver;
    this.pluginsManager = pluginsManager;
  }

  public void expand(final String parentName, final Class<?> paramType,
                     final List<Parameter> parameters, DocumentationType documentationType) {

    Set<String> beanPropNames = getBeanPropertyNames(paramType);
    List<Field> fields = getAllFields(paramType);
    LOG.debug("Expanding parameter type: {}", paramType);
    for (Field field : fields) {
      LOG.debug("Attempting to expanding field: {}", field);

      if (isStatic(field.getModifiers()) || field.isSynthetic() || !beanPropNames.contains(field.getName())) {
        LOG.debug("Skipping expansion of field: {}, not a valid bean property", field);
        continue;
      }
      Class<?> resolvedType = getResolvedType(field);
      if (!typeBelongsToJavaPackage(resolvedType) && !field.getType().isEnum()) {
        if (!field.getType().equals(paramType)) {
          LOG.debug("Expanding complex field: {} with type: {}", field, resolvedType);
          expand(nestedParentName(parentName, field), field.getType(), parameters, documentationType);
          continue;
        } else {
          LOG.warn("Skipping expanding complex field: {} with type: {} as it is recursively defined", field,
                  resolvedType);
        }
      }

      String dataTypeName = typeNameFor(resolvedType);

      if (dataTypeName == null) {
        dataTypeName = resolvedType.getSimpleName();
      }
      LOG.debug("Building parameter for field: {}, with type: ", field, resolvedType);
      ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(dataTypeName, parentName,
              field, documentationType, new ParameterBuilder());
      parameters.add(pluginsManager.expandParameter(parameterExpansionContext));

    }
  }

  private String nestedParentName(String parentName, Field field) {
    if (isNullOrEmpty(parentName)) {
      return field.getName();
    }
    return String.format("%s.%s", parentName, field.getName());
  }

  private Class<?> getResolvedType(Field field) {
    Class<?> type = field.getType();
    ResolvedType resolvedType = asResolved(resolver, type);
    ResolvedType alternativeType = alternateTypeProvider.alternateFor(resolvedType);
    Class<?> erasedType = alternativeType.getErasedType();
    if (type != erasedType) {
      LOG.debug("Found alternative type [{}] for field: [{}-{}]", erasedType, field, type);
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
      PropertyDescriptor[] propDescriptors = getBeanInfo(clazz).getPropertyDescriptors();

      for (PropertyDescriptor propDescriptor : propDescriptors) {

        if (propDescriptor.getReadMethod() != null && propDescriptor.getWriteMethod() != null) {
          beanProps.add(propDescriptor.getName());
        }
      }

      return beanProps;

    } catch (IntrospectionException e) {
      LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), e);
    }
    return newHashSet();
  }

  @VisibleForTesting
  BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
    return Introspector.getBeanInfo(clazz);
  }

}
