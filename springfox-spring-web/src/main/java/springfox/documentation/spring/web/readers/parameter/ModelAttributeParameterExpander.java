/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Predicates.*;
import static com.google.common.base.Strings.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.schema.Collections.*;
import static springfox.documentation.schema.Types.*;

@Component
public class ModelAttributeParameterExpander {
  private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
  private final FieldProvider fieldProvider;
  private final EnumTypeDeterminer enumTypeDeterminer;

  @Autowired
  protected DocumentationPluginsManager pluginsManager;

  @Autowired
  public ModelAttributeParameterExpander(
      FieldProvider fields,
      EnumTypeDeterminer enumTypeDeterminer) {
    
    this.fieldProvider = fields;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  public List<Parameter> expand(ExpansionContext context) {

    List<Parameter> parameters = newArrayList();
    Set<String> beanPropNames = getBeanPropertyNames(context.getParamType().getErasedType());
    Iterable<ResolvedField> fields = FluentIterable.from(fieldProvider.in(context.getParamType()))
        .filter(onlyBeanProperties(beanPropNames));
    LOG.debug("Expanding parameter type: {}", context.getParamType());
    AlternateTypeProvider alternateTypeProvider = context.getDocumentationContext().getAlternateTypeProvider();

    FluentIterable<ModelAttributeField> modelAttributes = from(fields)
        .transform(toModelAttributeField(alternateTypeProvider));

    FluentIterable<ModelAttributeField> expendables = modelAttributes
        .filter(not(simpleType()))
        .filter(not(recursiveType(context)));
    for (ModelAttributeField each : expendables) {
      LOG.debug("Attempting to expand expandable field: {}", each.getField());
      parameters.addAll(
          expand(
              context.childContext(
                  nestedParentName(context.getParentName(), each.getField()),
                  each.getFieldType(),
                  context.getDocumentationContext())));
    }

    FluentIterable<ModelAttributeField> collectionTypes = modelAttributes
        .filter(and(isCollection(), not(recursiveCollectionItemType(context.getParamType()))));
    for (ModelAttributeField each : collectionTypes) {
      LOG.debug("Attempting to expand collection/array field: {}", each.getField());

      ResolvedType itemType = collectionElementType(each.getFieldType());
      if (Types.isBaseType(itemType) || enumTypeDeterminer.isEnum(itemType.getErasedType())) {
        parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
      } else {
        parameters.addAll(
            expand(
                context.childContext(
                    nestedParentName(context.getParentName(), each.getField()),
                    itemType,
                    context.getDocumentationContext())));
      }
    }

    FluentIterable<ModelAttributeField> simpleFields = modelAttributes.filter(simpleType());
    for (ModelAttributeField each : simpleFields) {
      parameters.add(simpleFields(context.getParentName(), context.getDocumentationContext(), each));
    }
    return FluentIterable.from(parameters).filter(not(hiddenParameters())).toList();
  }

  private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return equal(collectionElementType(input.getFieldType()), paramType);
      }
    };
  }

  private Predicate<Parameter> hiddenParameters() {
    return new Predicate<Parameter>() {
      @Override
      public boolean apply(Parameter input) {
        return input.isHidden();
      }
    };
  }

  private Parameter simpleFields(
      String parentName,
      DocumentationContext documentationContext,
      ModelAttributeField each) {
    LOG.debug("Attempting to expand field: {}", each);
    String dataTypeName = Optional.fromNullable(typeNameFor(each.getFieldType().getErasedType()))
        .or(each.getFieldType().getErasedType().getSimpleName());
    LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
    ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(
        dataTypeName,
        parentName,
        each.getField(),
        documentationContext.getDocumentationType(),
        new ParameterBuilder());
    return pluginsManager.expandParameter(parameterExpansionContext);
  }


  private Predicate<ModelAttributeField> recursiveType(final ExpansionContext context) {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return context.hasSeenType(input.getFieldType());
      }
    };
  }

  private Predicate<ModelAttributeField> simpleType() {
    return and(not(isCollection()), not(isMap()),
        or(
            belongsToJavaPackage(),
            isBaseType(),
            isEnum()));
  }

  private Predicate<ModelAttributeField> isCollection() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return isContainerType(input.getFieldType());
      }
    };
  }

  private Predicate<ModelAttributeField> isMap() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return Maps.isMapType(input.getFieldType());
      }
    };
  }

  private Predicate<ModelAttributeField> isEnum() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return enumTypeDeterminer.isEnum(input.getFieldType().getErasedType());
      }
    };
  }

  private Predicate<ModelAttributeField> belongsToJavaPackage() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return ClassUtils.getPackageName(input.getFieldType().getErasedType()).startsWith("java.lang");
      }
    };
  }

  private Predicate<ModelAttributeField> isBaseType() {
    return new Predicate<ModelAttributeField>() {
      @Override
      public boolean apply(ModelAttributeField input) {
        return Types.isBaseType(input.getFieldType())
            || input.getField().getType().isPrimitive();
      }
    };
  }

  private Function<ResolvedField, ModelAttributeField> toModelAttributeField(
      final AlternateTypeProvider
          alternateTypeProvider) {
    return new Function<ResolvedField, ModelAttributeField>() {
      @Override
      public ModelAttributeField apply(ResolvedField input) {
        return new ModelAttributeField(fieldType(alternateTypeProvider, input), input);
      }
    };
  }

  private Predicate<ResolvedField> onlyBeanProperties(final Set<String> beanPropNames) {
    return new Predicate<ResolvedField>() {
      @Override
      public boolean apply(ResolvedField input) {
        return beanPropNames.contains(input.getName());
      }
    };
  }

  private String nestedParentName(String parentName, ResolvedField field) {
    String name = field.getName();
    ResolvedType fieldType = field.getType();
    if (isContainerType(fieldType) && !Types.isBaseType(collectionElementType(fieldType))) {
      name += "[0]";
    }

    if (isNullOrEmpty(parentName)) {
      return name;
    }
    return String.format("%s.%s", parentName, name);
  }

  private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedField field) {
    return alternateTypeProvider.alternateFor(field.getType());
  }

  private Set<String> getBeanPropertyNames(final Class<?> clazz) {

    try {
      Set<String> beanProps = new HashSet<String>();
      PropertyDescriptor[] propDescriptors = getBeanInfo(clazz).getPropertyDescriptors();

      for (PropertyDescriptor propDescriptor : propDescriptors) {

        if (propDescriptor.getReadMethod() != null) {
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
