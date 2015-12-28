/*
 *
 *  Copyright 2015 the original author or authors.
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
package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.bean.BeanModelProperty;
import springfox.documentation.schema.property.bean.ParameterModelProperty;
import springfox.documentation.schema.property.field.FieldModelProperty;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static springfox.documentation.schema.ResolvedTypes.*;
import static springfox.documentation.schema.property.BeanPropertyDefinitions.*;
import static springfox.documentation.schema.property.FactoryMethodProvider.*;
import static springfox.documentation.schema.property.bean.BeanModelProperty.*;
import static springfox.documentation.spi.schema.contexts.ModelContext.*;

@Primary
@Component("optimized")
public class OptimizedModelPropertiesProvider implements ModelPropertiesProvider {
  private static final Logger LOG = LoggerFactory.getLogger(OptimizedModelPropertiesProvider.class);
  private final AccessorsProvider accessors;
  private final FieldProvider fields;
  private final FactoryMethodProvider factoryMethods;
  private final TypeResolver typeResolver;
  private final BeanPropertyNamingStrategy namingStrategy;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;
  private ObjectMapper objectMapper;

  @Autowired
  public OptimizedModelPropertiesProvider(
      AccessorsProvider accessors,
      FieldProvider fields,
      FactoryMethodProvider factoryMethods,
      TypeResolver typeResolver,
      BeanPropertyNamingStrategy namingStrategy,
      SchemaPluginsManager schemaPluginsManager,
      TypeNameExtractor typeNameExtractor) {

    this.accessors = accessors;
    this.fields = fields;
    this.factoryMethods = factoryMethods;
    this.typeResolver = typeResolver;
    this.namingStrategy = namingStrategy;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    objectMapper = event.getObjectMapper();
  }


  @Override
  public List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext) {
    List<ModelProperty> properties = newArrayList();
    BeanDescription beanDescription = beanDescription(type, givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
        BeanPropertyDefinitions.beanPropertyByInternalName());
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {
      LOG.debug("Reading property {}", each.getKey());
      BeanPropertyDefinition jacksonProperty = each.getValue();
      Optional<AnnotatedMember> annotatedMember = Optional.fromNullable(jacksonProperty.getPrimaryMember());
      if (annotatedMember.isPresent()) {
        properties.addAll(candidateProperties(type, annotatedMember.get(), jacksonProperty, givenContext));
      }
    }
    return properties;
  }

  private Function<ResolvedMethod, List<ModelProperty>> propertyFromBean(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty) {

    return new Function<ResolvedMethod, List<ModelProperty>>() {
      @Override
      public List<ModelProperty> apply(ResolvedMethod input) {
        ResolvedType type = paramOrReturnType(typeResolver, input);
        if (!givenContext.hasSeenBefore(type)) {
          if (shouldUnwrap(input)) {
            return propertiesFor(type, fromParent(givenContext, type));
          }
          return newArrayList(beanModelProperty(input, jacksonProperty, givenContext));
        }
        return newArrayList();
      }
    };
  }


  private boolean shouldUnwrap(ResolvedMethod input) {
    return any(newArrayList(input.getRawMember().getDeclaredAnnotations()), ofType(JsonUnwrapped.class));
  }

  private Function<ResolvedField, List<ModelProperty>> propertyFromField(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty) {

    return new Function<ResolvedField, List<ModelProperty>>() {
      @Override
      public List<ModelProperty> apply(ResolvedField input) {
        List<Annotation> annotations = newArrayList(input.getRawMember().getAnnotations());
        if (any(annotations, ofType(JsonUnwrapped.class))) {
          return propertiesFor(input.getType(), ModelContext.fromParent(givenContext, input.getType()));
        }
        return newArrayList(fieldModelProperty(input, jacksonProperty, givenContext));
      }
    };
  }

  private Predicate<? super Annotation> ofType(final Class<?> annotationType) {
    return new Predicate<Annotation>() {
      @Override
      public boolean apply(Annotation input) {
        return annotationType.isAssignableFrom(input.getClass());
      }
    };
  }

  @VisibleForTesting
  List<ModelProperty> candidateProperties(
      ResolvedType type,
      AnnotatedMember member,
      BeanPropertyDefinition jacksonProperty,
      ModelContext givenContext) {

    List<ModelProperty> properties = newArrayList();
    if (member instanceof AnnotatedMethod) {
      properties.addAll(findAccessorMethod(type, member)
          .transform(propertyFromBean(givenContext, jacksonProperty))
          .or(new ArrayList<ModelProperty>()));
    } else if (member instanceof AnnotatedField) {
      properties.addAll(findField(type, jacksonProperty.getInternalName())
          .transform(propertyFromField(givenContext, jacksonProperty))
          .or(new ArrayList<ModelProperty>()));
    } else if (member instanceof AnnotatedParameter) {
      ModelContext modelContext = ModelContext.fromParent(givenContext, type);
      properties.addAll(fromFactoryMethod(type, jacksonProperty, (AnnotatedParameter) member, modelContext));
    }
    return from(properties).filter(hiddenProperties()).toList();

  }

  private Predicate<? super ModelProperty> hiddenProperties() {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean apply(ModelProperty input) {
        return !input.isHidden();
      }
    };
  }

  private Optional<ResolvedField> findField(ResolvedType resolvedType,
                                            final String fieldName) {
    return tryFind(fields.in(resolvedType), new Predicate<ResolvedField>() {
      public boolean apply(ResolvedField input) {
        return fieldName.equals(input.getName());
      }
    });
  }

  private ModelProperty fieldModelProperty(ResolvedField childField, BeanPropertyDefinition jacksonProperty,
                                           ModelContext modelContext) {
    String fieldName = name(jacksonProperty, modelContext.isReturnType(), namingStrategy);
    FieldModelProperty fieldModelProperty = new FieldModelProperty(fieldName, childField,
        modelContext.getAlternateTypeProvider());
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(fieldModelProperty.getName())
        .type(fieldModelProperty.getType())
        .qualifiedType(fieldModelProperty.qualifiedTypeName())
        .position(fieldModelProperty.position())
        .required(fieldModelProperty.isRequired())
        .description(fieldModelProperty.propertyDescription())
        .allowableValues(fieldModelProperty.allowableValues());
    return schemaPluginsManager.property(
        new ModelPropertyContext(propertyBuilder,
            childField.getRawMember(),
            typeResolver,
            modelContext.getDocumentationType()))
        .updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
  }

  private ModelProperty beanModelProperty(
      ResolvedMethod childProperty,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext) {

    String propertyName = name(jacksonProperty, modelContext.isReturnType(), namingStrategy);
    BeanModelProperty beanModelProperty
        = new BeanModelProperty(propertyName, childProperty, typeResolver, modelContext.getAlternateTypeProvider());

    LOG.debug("Adding property {} to model", propertyName);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(beanModelProperty.getName())
        .type(beanModelProperty.getType())
        .qualifiedType(beanModelProperty.qualifiedTypeName())
        .position(beanModelProperty.position())
        .required(beanModelProperty.isRequired())
        .isHidden(false)
        .description(beanModelProperty.propertyDescription())
        .allowableValues(beanModelProperty.allowableValues());
    return schemaPluginsManager.property(
        new ModelPropertyContext(propertyBuilder,
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType()))
        .updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
  }

  private ModelProperty paramModelProperty(
      ResolvedParameterizedMember constructor,
      BeanPropertyDefinition jacksonProperty,
      AnnotatedParameter parameter,
      ModelContext modelContext) {

    String propertyName = name(jacksonProperty, modelContext.isReturnType(), namingStrategy);
    ParameterModelProperty beanModelProperty
        = new ParameterModelProperty(propertyName, parameter, constructor, modelContext
        .getAlternateTypeProvider());

    LOG.debug("Adding property {} to model", propertyName);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(beanModelProperty.getName())
        .type(beanModelProperty.getType())
        .qualifiedType(beanModelProperty.qualifiedTypeName())
        .position(beanModelProperty.position())
        .required(beanModelProperty.isRequired())
        .isHidden(false)
        .description(beanModelProperty.propertyDescription())
        .allowableValues(beanModelProperty.allowableValues());
    return schemaPluginsManager.property(
        new ModelPropertyContext(propertyBuilder,
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType()))
        .updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
  }

  private Optional<ResolvedMethod> findAccessorMethod(ResolvedType resolvedType, final AnnotatedMember member) {
    return tryFind(accessors.in(resolvedType), new Predicate<ResolvedMethod>() {
      public boolean apply(ResolvedMethod accessorMethod) {
        return accessorMethod.getRawMember().equals(member.getMember());
      }
    });
  }

  private List<ModelProperty> fromFactoryMethod(
      final ResolvedType resolvedType,
      final BeanPropertyDefinition beanProperty,
      final AnnotatedParameter member,
      final ModelContext givenContext) {

    Optional<ModelProperty> property = factoryMethods.in(resolvedType, factoryMethodOf(member))
        .transform(new Function<ResolvedParameterizedMember, ModelProperty>() {
          @Override
          public ModelProperty apply(ResolvedParameterizedMember input) {
            return paramModelProperty(input, beanProperty, member, givenContext);
          }
        });
    if (property.isPresent()) {
      return newArrayList(property.get());
    }
    return newArrayList();
  }

  private BeanDescription beanDescription(ResolvedType type, ModelContext context) {
    if (context.isReturnType()) {
      SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
      return serializationConfig.introspect(TypeFactory.defaultInstance()
          .constructType(type.getErasedType()));
    } else {
      DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
      return serializationConfig.introspect(TypeFactory.defaultInstance()
          .constructType(type.getErasedType()));
    }
  }
}
