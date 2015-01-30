package com.mangofactory.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.documentation.service.model.Parameter;
import com.mangofactory.documentation.service.model.builder.ParameterBuilder;
import com.mangofactory.documentation.spi.schema.AlternateTypeProvider;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContext;
import com.mangofactory.documentation.spi.service.contexts.ParameterExpansionContext;
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager;
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

import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.schema.ResolvedTypes.*;
import static com.mangofactory.documentation.schema.Types.*;
import static java.lang.reflect.Modifier.*;

@Component
public class ModelAttributeParameterExpander {
  private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private final TypeResolver resolver;
  private final DocumentationPluginsManager pluginsManager;

  @Autowired
  public ModelAttributeParameterExpander(TypeResolver resolver,
                                         DocumentationPluginsManager pluginsManager) {
    this.resolver = resolver;
    this.pluginsManager = pluginsManager;
  }

  public void expand(final String parentName, final Class<?> paramType,
                     final List<Parameter> parameters, DocumentationContext documentationContext) {

    Set<String> beanPropNames = getBeanPropertyNames(paramType);
    Iterable<Field> fields = FluentIterable.from(getInstanceFields(paramType))
            .filter(onlyBeanProperties(beanPropNames));
    LOG.debug("Expanding parameter type: {}", paramType);
    for (Field field : fields) {
      LOG.debug("Attempting to expanding field: {}", field);

      Class<?> resolvedType = getResolvedType(documentationContext.getAlternateTypeProvider(), field);
      if (!typeBelongsToJavaPackage(resolvedType) && !field.getType().isEnum()) {
        if (!field.getType().equals(paramType)) {
          LOG.debug("Expanding complex field: {} with type: {}", field, resolvedType);
          expand(nestedParentName(parentName, field), field.getType(), parameters, documentationContext);
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
              field, documentationContext.getDocumentationType(), new ParameterBuilder());
      parameters.add(pluginsManager.expandParameter(parameterExpansionContext));

    }
  }

  private Predicate<Field> onlyBeanProperties(final Set<String> beanPropNames) {
    return new Predicate<Field>() {
      @Override
      public boolean apply(Field input) {
        return beanPropNames.contains(input.getName());
      }
    };
  }

  private String nestedParentName(String parentName, Field field) {
    if (isNullOrEmpty(parentName)) {
      return field.getName();
    }
    return String.format("%s.%s", parentName, field.getName());
  }

  private Class<?> getResolvedType(AlternateTypeProvider alternateTypeProvider, Field field) {
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

  private List<Field> getInstanceFields(final Class<?> type) {

    List<Field> result = new ArrayList<Field>();

    Class<?> i = type;
    while (!rootType(i)) {
      result.addAll(Arrays.asList(i.getDeclaredFields()));
      i = i.getSuperclass();
    }
    return FluentIterable.from(result)
            .filter(not(staticField()))
            .filter(not(syntheticFields()))
            .toList();
  }

  private Predicate<Field> syntheticFields() {
    return new Predicate<Field>() {
      @Override
      public boolean apply(Field input) {
        return input.isSynthetic();
      }
    };
  }

  private Predicate<Field> staticField() {
    return new Predicate<Field>() {
      @Override
      public boolean apply(Field input) {
        return isStatic(input.getModifiers());
      }
    };
  }

  private boolean rootType(Class<?> i) {
    return i == null || i == Object.class;
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
