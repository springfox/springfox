/*
 *
 *  Copyright 2015-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.*;
import static java.lang.reflect.Modifier.*;
import static springfox.documentation.schema.Collections.collectionElementType;
import static springfox.documentation.schema.Collections.isContainerType;
import static springfox.documentation.schema.Types.*;

@Component
public class ModelAttributeParameterExpander {
  private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private final TypeResolver resolver;
  @Autowired
  protected DocumentationPluginsManager pluginsManager;

  @Autowired
  public ModelAttributeParameterExpander(TypeResolver resolver) {
    this.resolver = resolver;
  }

  public void expand(
      final String parentName,
      final Class<?> paramType,
      final List<Parameter> parameters,
      DocumentationContext documentationContext) {

    Set<String> beanPropNames = getBeanPropertyNames(paramType);
    Iterable<Field> fields = from(getInstanceFields(paramType))
            .filter(onlyBeanProperties(beanPropNames));
    LOG.debug("Expanding parameter type: {}", paramType);
    AlternateTypeProvider alternateTypeProvider = documentationContext.getAlternateTypeProvider();
    FluentIterable<ModelAttributeField> expendables = from(fields)
            .transform(toModelAttributeField(alternateTypeProvider))
            .filter(not(simpleType()))
            .filter(not(recursiveType(paramType)));
    for (ModelAttributeField each : expendables) {
      LOG.debug("Attempting to expand expandable field: {}", each.getField());
      expand(nestedParentName(parentName, each.getField()), each.getFieldType(), parameters, documentationContext);
    }
    FluentIterable<ModelAttributeField> simpleFields = from(fields)
            .transform(toModelAttributeField(alternateTypeProvider))
            .filter(simpleType());
    for (ModelAttributeField each : simpleFields) {
      LOG.debug("Attempting to expand field: {}", each);
      String dataTypeName = Optional.fromNullable(typeNameFor(each.getFieldType()))
              .or(each.getFieldType().getSimpleName());
      LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
      ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
          dataTypeName,
          parentName,
          each.getField(),
          documentationContext.getDocumentationType(),
          new ParameterBuilder());
      parameters.add(pluginsManager.expandParameter(parameterExpansionContext));
    }
  }

  private Predicate<ModelAttributeField> recursiveType(final Class<?> paramType) {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return input.getFieldType() == paramType;
      }
    };
  }

  private Predicate<ModelAttributeField> simpleType() {
    return or(
            or(belongsToJavaPackage(),
              or(isCollection(), isMap())),
            isEnum());
  }

  private Predicate<ModelAttributeField> isCollection() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        Class<?> fieldType = input.getFieldType();
        return isCollection(fieldType);
      }
    };
  }

  private boolean isCollection(Class<?> fieldType) {
    return Collection.class.isAssignableFrom(fieldType) || fieldType.isArray();
  }

  private Predicate<ModelAttributeField> isMap() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return Map.class.isAssignableFrom(input.getFieldType());
      }
    };
  }

  private Predicate<ModelAttributeField> isEnum() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return input.getFieldType().isEnum();
      }
    };
  }

  private Predicate<ModelAttributeField> belongsToJavaPackage() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return packageName(input.getFieldType()).startsWith("java");
      }
    };
  }

  private Function<Field, ModelAttributeField> toModelAttributeField(final AlternateTypeProvider
                                                                             alternateTypeProvider) {
    return new Function<Field, ModelAttributeField>() {
      @Override
      public ModelAttributeField apply(Field input) {
        return new ModelAttributeField(fieldType(alternateTypeProvider, input), input);
      }
    };
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
	String name = field.getName();
	if (isCollection(field.getType())) name += "[#ind]";
	
    if (isNullOrEmpty(parentName)) {
      return name;
    }
    return String.format("%s.%s", parentName, name);
  }

  private Class<?> fieldType(AlternateTypeProvider alternateTypeProvider, Field field) {
    Class<?> type = field.getType();
    ResolvedType resolvedType = resolver.resolve(type);
    
    if (isContainerType(resolvedType)) {
        try {
    	    if (type.isArray()) {
    	        resolvedType = resolver.arrayType(type.getComponentType());
    	    } else {
    	        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
    	        Optional<Type> itemClazz = FluentIterable.from(newArrayList(parameterizedType.getActualTypeArguments())).first();
    	          if (itemClazz.isPresent()) {
    	        	  resolvedType = resolver.resolve(type, itemClazz.get());
    	          }
    	        }
    	      } catch (Exception e) {
    	    	  LOG.warn(String.format("Failed to get generic type of field (%s)", field.getName()), e);
    	      }
        resolvedType = collectionElementType(resolvedType);
    }
    
    ResolvedType alternativeType = alternateTypeProvider.alternateFor(resolvedType);
    Class<?> erasedType = alternativeType.getErasedType();
    if (type != erasedType) {
      LOG.debug("Found alternative type [{}] for field: [{}-{}]", erasedType, field, type);
    }
    return erasedType;
  }

  private String packageName(Class<?> type) {
    return Optional.fromNullable(type.getPackage()).transform(toPackageName()).or("java");
  }

  private Function<Package, String> toPackageName() {
    return new Function<Package, String>() {
      @Override
      public String apply(Package input) {
        return input.getName();
      }
    };
  }

  private List<Field> getInstanceFields(final Class<?> type) {

    List<Field> result = new ArrayList<Field>();

    Class<?> i = type;
    while (!rootType(i)) {
      result.addAll(Arrays.asList(i.getDeclaredFields()));
      i = i.getSuperclass();
    }
    return from(result)
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

  private boolean rootType(Class clazz) {
    return Optional.fromNullable(clazz).or(Object.class) == Object.class;
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
