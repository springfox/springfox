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
package springfox.documentation.schema.property;

import static springfox.documentation.schema.Annotations.memberIsUnwrapped;
import static springfox.documentation.schema.ResolvedTypes.modelRefFactory;
import static springfox.documentation.schema.property.BeanPropertyDefinitions.name;
import static springfox.documentation.schema.property.FactoryMethodProvider.factoryMethodOf;
import static springfox.documentation.schema.property.bean.BeanModelProperty.paramOrReturnType;
import static springfox.documentation.spi.schema.contexts.ModelContext.fromParent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.schema.Maps;
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
    List<ModelProperty> properties = new ArrayList<>();
    BeanDescription beanDescription = beanDescription(type, givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = Maps.uniqueIndex(beanDescription.findProperties(),
        BeanPropertyDefinitions.beanPropertyByInternalName());
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {
      LOG.debug("Reading property {}", each.getKey());
      BeanPropertyDefinition jacksonProperty = each.getValue();
      Optional<AnnotatedMember> annotatedMember
          = Optional.ofNullable(safeGetPrimaryMember(jacksonProperty));
      if (annotatedMember.isPresent()) {
        properties.addAll(candidateProperties(type, annotatedMember.get(), jacksonProperty, givenContext));
      }
    }
    return properties.stream().sorted(byPropertyName()).collect(Collectors.toList());
  }

  

private Comparator<ModelProperty> byPropertyName() {
    return new Comparator<ModelProperty>() {
      @Override
      public int compare(ModelProperty first, ModelProperty second) {
        return first.getName().compareTo(second.getName());
      }
    };
  }

  private AnnotatedMember safeGetPrimaryMember(BeanPropertyDefinition jacksonProperty) {
    try {
      return jacksonProperty.getPrimaryMember();
    } catch (IllegalArgumentException e) {
      LOG.warn(String.format("Unable to get unique property. %s", e.getMessage()));
      return null;
    }
  }

  private Function<ResolvedMethod, List<ModelProperty>> propertyFromBean(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty) {

    return new Function<ResolvedMethod, List<ModelProperty>>() {
      @Override
      public List<ModelProperty> apply(ResolvedMethod input) {
        ResolvedType type = paramOrReturnType(typeResolver, input);
        if (!givenContext.canIgnore(type)) {
          if (memberIsUnwrapped(jacksonProperty.getPrimaryMember())) {
              return propertiesFor(type, fromParent(givenContext, type));
          }
          return new ArrayList<>(Arrays.asList(beanModelProperty(input, jacksonProperty, givenContext)));
        }
        return new ArrayList<>();
      }
    };
  }


  private Function<ResolvedField, List<ModelProperty>> propertyFromField(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty) {

    return new Function<ResolvedField, List<ModelProperty>>() {
      @Override
      public List<ModelProperty> apply(ResolvedField input) {
        if (!givenContext.canIgnore(input.getType())) {
          if (memberIsUnwrapped(jacksonProperty.getField())) {
              return propertiesFor(input.getType(), ModelContext.fromParent(givenContext, input.getType()));
          }
          return new ArrayList<>(Arrays.asList(fieldModelProperty(input, jacksonProperty, givenContext)));
        }
        return new ArrayList<>();
      }
    };
  }

  private Predicate<? super Annotation> ofType(final Class<?> annotationType) {
    return new Predicate<Annotation>() {
      @Override
      public boolean test(Annotation input) {
        return annotationType.isAssignableFrom(input.getClass());
      }
    };
  }

  List<ModelProperty> candidateProperties(
      ResolvedType type,
      AnnotatedMember member,
      BeanPropertyDefinition jacksonProperty,
      ModelContext givenContext) {

    List<ModelProperty> properties = new ArrayList<>();
    if (member instanceof AnnotatedMethod) {
      properties.addAll(findAccessorMethod(type, member)
          .map(propertyFromBean(givenContext, jacksonProperty))
          .orElse(new ArrayList<ModelProperty>()));
    } else if (member instanceof AnnotatedField) {
      properties.addAll(findField(type, jacksonProperty.getInternalName())
          .map(propertyFromField(givenContext, jacksonProperty))
          .orElse(new ArrayList<ModelProperty>()));
    } else if (member instanceof AnnotatedParameter) {
      ModelContext modelContext = ModelContext.fromParent(givenContext, type);
      properties.addAll(fromFactoryMethod(type, jacksonProperty, (AnnotatedParameter) member, modelContext));
    }
    return properties.stream().filter(hiddenProperties()).collect(Collectors.toList());

  }

  private Predicate<? super ModelProperty> hiddenProperties() {
    return new Predicate<ModelProperty>() {
      @Override
      public boolean test(ModelProperty input) {
        return !input.isHidden();
      }
    };
  }

  private Optional<ResolvedField> findField(ResolvedType resolvedType, final String fieldName) {
    return fields.in(resolvedType).stream().filter(input -> fieldName.equals(input.getName())).findFirst();
  }

  private ModelProperty fieldModelProperty(
      ResolvedField childField,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext) {
    String fieldName = name(jacksonProperty, modelContext.isReturnType(), namingStrategy);
    FieldModelProperty fieldModelProperty
        = new FieldModelProperty(
            fieldName,
            childField,
            typeResolver,
            modelContext.getAlternateTypeProvider(),
            jacksonProperty);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(fieldModelProperty.getName())
        .type(fieldModelProperty.getType())
        .qualifiedType(fieldModelProperty.qualifiedTypeName())
        .position(fieldModelProperty.position())
        .required(fieldModelProperty.isRequired())
        .description(fieldModelProperty.propertyDescription())
        .allowableValues(fieldModelProperty.allowableValues())
        .example(fieldModelProperty.example());
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
        = new BeanModelProperty(
        propertyName,
        childProperty,
        typeResolver,
        modelContext.getAlternateTypeProvider(),
        jacksonProperty);

    LOG.debug("Adding property {} to model", propertyName);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(beanModelProperty.getName())
        .type(beanModelProperty.getType())
        .qualifiedType(beanModelProperty.qualifiedTypeName())
        .position(beanModelProperty.position())
        .required(beanModelProperty.isRequired())
        .isHidden(false)
        .description(beanModelProperty.propertyDescription())
        .allowableValues(beanModelProperty.allowableValues())
        .example(beanModelProperty.example());
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
    ParameterModelProperty parameterModelProperty
        = new ParameterModelProperty(
            propertyName,
            parameter,
            constructor,
            typeResolver,
            modelContext.getAlternateTypeProvider(),
            jacksonProperty);

    LOG.debug("Adding property {} to model", propertyName);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
        .name(parameterModelProperty.getName())
        .type(parameterModelProperty.getType())
        .qualifiedType(parameterModelProperty.qualifiedTypeName())
        .position(parameterModelProperty.position())
        .required(parameterModelProperty.isRequired())
        .isHidden(false)
        .description(parameterModelProperty.propertyDescription())
        .allowableValues(parameterModelProperty.allowableValues())
        .example(parameterModelProperty.example());
    return schemaPluginsManager.property(
        new ModelPropertyContext(propertyBuilder,
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType()))
        .updateModelRef(modelRefFactory(modelContext, typeNameExtractor));
  }

  private Optional<ResolvedMethod> findAccessorMethod(ResolvedType resolvedType, final AnnotatedMember member) {
      return accessors.in(resolvedType).stream().filter(new Predicate<ResolvedMethod>() {
        public boolean test(ResolvedMethod accessorMethod) {
          return SimpleMethodSignatureEquality.equivalent(accessorMethod.getRawMember(), (Method) member.getMember());
        }
      }).findFirst();
  }

  private List<ModelProperty> fromFactoryMethod(
      final ResolvedType resolvedType,
      final BeanPropertyDefinition beanProperty,
      final AnnotatedParameter member,
      final ModelContext givenContext) {

    Optional<ModelProperty> property = factoryMethods.in(resolvedType, factoryMethodOf(member))
        .map(new Function<ResolvedParameterizedMember, ModelProperty>() {
          @Override
          public ModelProperty apply(ResolvedParameterizedMember input) {
            return paramModelProperty(input, beanProperty, member, givenContext);
          }
        });
    if (property.isPresent()) {
      return new ArrayList<>(Arrays.asList(property.get()));
    }
    return new ArrayList<>();
  }

  private BeanDescription beanDescription(ResolvedType type, ModelContext context) {
    if (context.isReturnType()) {
      SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
      return serializationConfig.introspect(serializationConfig.constructType(type.getErasedType()));
    } else {
      DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
      return serializationConfig.introspect(serializationConfig.constructType(type.getErasedType()));
    }
  }
}
