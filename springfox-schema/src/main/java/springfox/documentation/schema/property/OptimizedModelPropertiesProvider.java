/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelPropertyBuilder;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.schema.plugins.SchemaPluginsManager;
import springfox.documentation.schema.property.bean.AccessorsProvider;
import springfox.documentation.schema.property.bean.BeanModelProperty;
import springfox.documentation.schema.property.bean.ParameterModelProperty;
import springfox.documentation.schema.property.field.FieldModelProperty;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.*;
import static java.util.Optional.*;
import static java.util.function.Function.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.schema.Annotations.*;
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
  private final JacksonAnnotationIntrospector annotationIntrospector;
  private final EnumTypeDeterminer enumTypeDeterminer;
  private final TypeNameExtractor typeNameExtractor;
  private ObjectMapper objectMapper;

  @SuppressWarnings("ParameterNumber")
  @Autowired
  public OptimizedModelPropertiesProvider(
      AccessorsProvider accessors,
      FieldProvider fields,
      FactoryMethodProvider factoryMethods,
      TypeResolver typeResolver,
      BeanPropertyNamingStrategy namingStrategy,
      SchemaPluginsManager schemaPluginsManager,
      EnumTypeDeterminer enumTypeDeterminer,
      TypeNameExtractor typeNameExtractor) {

    this.accessors = accessors;
    this.fields = fields;
    this.factoryMethods = factoryMethods;
    this.typeResolver = typeResolver;
    this.namingStrategy = namingStrategy;
    this.schemaPluginsManager = schemaPluginsManager;
    this.annotationIntrospector = new JacksonAnnotationIntrospector();
    this.enumTypeDeterminer = enumTypeDeterminer;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    objectMapper = event.getObjectMapper();
  }


  @Override
  public List<ModelProperty> propertiesFor(
      ResolvedType type,
      ModelContext givenContext) {
    List<ModelProperty> syntheticProperties = schemaPluginsManager.syntheticProperties(givenContext);
    if (!syntheticProperties.isEmpty()) {
      return syntheticProperties;
    }
    return propertiesFor(
        type,
        givenContext,
        "");
  }

  @Override
  public List<PropertySpecification> propertySpecificationsFor(
      ResolvedType type,
      ModelContext givenContext) {
    List<PropertySpecification> syntheticProperties =
        schemaPluginsManager.syntheticPropertySpecifications(givenContext);
    if (!syntheticProperties.isEmpty()) {
      return syntheticProperties;
    }
    return propertiesSpecificationsFor(
        type,
        givenContext,
        "");
  }

  // List cannot contain duplicated byPropertyName()
  private List<ModelProperty> propertiesFor(
      ResolvedType type,
      ModelContext givenContext,
      String namePrefix) {
    Set<ModelProperty> properties = new TreeSet<>(byPropertyName());
    BeanDescription beanDescription = beanDescription(
        type,
        givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = beanDescription.findProperties().stream().collect(toMap(
        beanPropertyByInternalName(),
        identity()));
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {
      LOG.debug(
          "Reading property {}",
          each.getKey());
      BeanPropertyDefinition jacksonProperty = each.getValue();
      Optional<AnnotatedMember> annotatedMember
          = ofNullable(safeGetPrimaryMember(
          jacksonProperty,
          givenContext));
      annotatedMember.ifPresent(
          member -> properties.addAll(
              candidateProperties(
                  type,
                  member,
                  jacksonProperty,
                  givenContext,
                  namePrefix)));
    }
    return new ArrayList<>(properties);
  }

  private List<PropertySpecification> propertySpecificationsFor(
      ResolvedType type,
      ModelContext givenContext,
      String namePrefix) {
    Set<PropertySpecification> properties = new TreeSet<>(Comparator.comparing(PropertySpecification::getName));
    BeanDescription beanDescription = beanDescription(
        type,
        givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = beanDescription.findProperties().stream().collect(toMap(
        beanPropertyByInternalName(),
        identity()));
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {
      LOG.debug(
          "Reading property {}",
          each.getKey());
      BeanPropertyDefinition jacksonProperty = each.getValue();
      Optional<AnnotatedMember> annotatedMember
          = ofNullable(safeGetPrimaryMember(
          jacksonProperty,
          givenContext));
      annotatedMember.ifPresent(
          member -> properties.addAll(
              candidatePropertySpecifications(
                  type,
                  member,
                  jacksonProperty,
                  givenContext,
                  namePrefix)));
    }
    return new ArrayList<>(properties);
  }

  private List<PropertySpecification> propertiesSpecificationsFor(
      ResolvedType type,
      ModelContext givenContext,
      String namePrefix) {
    Set<PropertySpecification> properties = new TreeSet<>(Comparator.comparing(PropertySpecification::getName));
    BeanDescription beanDescription = beanDescription(
        type,
        givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = beanDescription.findProperties().stream().collect(toMap(
        beanPropertyByInternalName(),
        identity()));
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {
      LOG.debug(
          "Reading property {}",
          each.getKey());
      BeanPropertyDefinition jacksonProperty = each.getValue();
      Optional<AnnotatedMember> annotatedMember
          = ofNullable(safeGetPrimaryMember(
          jacksonProperty,
          givenContext));
      annotatedMember.ifPresent(
          member -> properties.addAll(
              candidatePropertySpecifications(
                  type,
                  member,
                  jacksonProperty,
                  givenContext,
                  namePrefix)));
    }
    return new ArrayList<>(properties);
  }

  private Comparator<ModelProperty> byPropertyName() {
    return Comparator.comparing(ModelProperty::getName);
  }

  private AnnotatedMember safeGetPrimaryMember(
      BeanPropertyDefinition jacksonProperty,
      ModelContext givenContext) {
    try {
        /* was jacksonProperty.getPrimaryMember() but as of jackson-binding:2.9+
        includes setter-less properties returning false positives so returning
        back to original getPrimaryMember() implementation */
      if (givenContext.isReturnType()) {
        return jacksonProperty.getAccessor();
      }
      return jacksonProperty.getMutator();
    } catch (IllegalArgumentException e) {
      LOG.warn(String.format(
          "Unable to get unique property. %s",
          e.getMessage()));
      return null;
    }
  }

  private Function<ResolvedMethod, List<ModelProperty>> propertyFromBean(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty,
      final String namePrefix) {

    return input -> {
      ResolvedType type = paramOrReturnType(
          typeResolver,
          input);
      if (!givenContext.canIgnore(type)) {
        if (memberIsUnwrapped(jacksonProperty.getPrimaryMember())) {
          return propertiesFor(
              type,
              fromParent(
                  givenContext,
                  type),
              String.format(
                  "%s%s",
                  namePrefix,
                  unwrappedPrefix(jacksonProperty.getPrimaryMember())));
        }
        return singletonList(beanModelProperty(
            input,
            jacksonProperty,
            givenContext,
            namePrefix));
      }
      return new ArrayList<>();
    };
  }

  private Function<ResolvedMethod, List<PropertySpecification>> propertySpecificationFromBean(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty,
      final String namePrefix) {

    return input -> {
      ResolvedType type = paramOrReturnType(
          typeResolver,
          input);
      if (!givenContext.canIgnore(type)) {
        if (memberIsUnwrapped(jacksonProperty.getPrimaryMember())) {
          return propertySpecificationsFor(
              type,
              fromParent(
                  givenContext,
                  type),
              String.format(
                  "%s%s",
                  namePrefix,
                  unwrappedPrefix(jacksonProperty.getPrimaryMember())));
        }
        return singletonList(beanModelPropertySpecification(
            input,
            jacksonProperty,
            givenContext,
            namePrefix));
      }
      return new ArrayList<>();
    };
  }

  private Function<ResolvedField, List<ModelProperty>> propertyFromField(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty,
      final String namePrefix) {

    return input -> {
      if (!givenContext.canIgnore(input.getType())) {
        if (memberIsUnwrapped(jacksonProperty.getField())) {
          return propertiesFor(
              input.getType(),
              ModelContext.fromParent(
                  givenContext,
                  input.getType()),
              String.format(
                  "%s%s",
                  namePrefix,
                  unwrappedPrefix(jacksonProperty.getPrimaryMember())));
        }
        return singletonList(fieldModelProperty(
            input,
            jacksonProperty,
            givenContext,
            namePrefix));
      }
      return new ArrayList<>();
    };
  }

  private Function<ResolvedField, List<PropertySpecification>> propertySpecificationFromField(
      final ModelContext givenContext,
      final BeanPropertyDefinition jacksonProperty,
      final String namePrefix) {

    return input -> {
      if (!givenContext.canIgnore(input.getType())) {
        if (memberIsUnwrapped(jacksonProperty.getField())) {
          return propertySpecificationsFor(
              input.getType(),
              ModelContext.fromParent(
                  givenContext,
                  input.getType()),
              String.format(
                  "%s%s",
                  namePrefix,
                  unwrappedPrefix(jacksonProperty.getPrimaryMember())));
        }
        return singletonList(fieldModelPropertySpecification(
            input,
            jacksonProperty,
            givenContext,
            namePrefix));
      }
      return new ArrayList<>();
    };
  }

  private List<ModelProperty> candidateProperties(
      ResolvedType type,
      AnnotatedMember member,
      BeanPropertyDefinition jacksonProperty,
      ModelContext givenContext,
      String namePrefix) {

    List<ModelProperty> properties = new ArrayList<>();
    if (!isInActiveView(
        member,
        givenContext)) {
      return properties;
    }

    if (member instanceof AnnotatedMethod) {
      properties.addAll(
          findAccessorMethod(
              type,
              member)
              .map(propertyFromBean(
                  givenContext,
                  jacksonProperty,
                  namePrefix))
              .orElse(new ArrayList<>()));
    } else if (member instanceof AnnotatedField) {
      properties.addAll(findField(
          type,
          jacksonProperty.getInternalName())
                            .map(propertyFromField(
                                givenContext,
                                jacksonProperty,
                                namePrefix))
                            .orElse(new ArrayList<>()));
    } else if (member instanceof AnnotatedParameter) {
      ModelContext modelContext = ModelContext.fromParent(
          givenContext,
          type);
      properties.addAll(
          fromFactoryMethod(
              type,
              jacksonProperty,
              (AnnotatedParameter) member,
              modelContext,
              namePrefix));
    }
    return properties.stream()
        .filter(hiddenProperties())
        .collect(toList());
  }

  private List<PropertySpecification> candidatePropertySpecifications(
      ResolvedType type,
      AnnotatedMember member,
      BeanPropertyDefinition jacksonProperty,
      ModelContext givenContext,
      String namePrefix) {

    List<PropertySpecification> properties = new ArrayList<>();
    if (!isInActiveView(
        member,
        givenContext)) {
      return properties;
    }

    if (member instanceof AnnotatedMethod) {
      properties.addAll(
          findAccessorMethod(
              type,
              member)
              .map(propertySpecificationFromBean(
                  givenContext,
                  jacksonProperty,
                  namePrefix))
              .orElse(new ArrayList<>()));
    } else if (member instanceof AnnotatedField) {
      properties.addAll(findField(
          type,
          jacksonProperty.getInternalName())
                            .map(propertySpecificationFromField(
                                givenContext,
                                jacksonProperty,
                                namePrefix))
                            .orElse(new ArrayList<>()));
    } else if (member instanceof AnnotatedParameter) {
      ModelContext modelContext = ModelContext.fromParent(
          givenContext,
          type);
      properties.addAll(
          specificationFromFactoryMethod(
              type,
              jacksonProperty,
              (AnnotatedParameter) member,
              modelContext,
              namePrefix));
    }
    return properties.stream()
        .filter(input -> !input.getHidden())
        .collect(Collectors.toList());
  }

  private boolean isInActiveView(
      AnnotatedMember member,
      ModelContext givenContext) {
    if (givenContext.getView().isPresent()) {
      Class<?>[] typeViews = annotationIntrospector.findViews(member);
      if (typeViews == null) {
        typeViews = new Class<?>[0];
      }
      if (typeViews.length == 0 && objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
        return true;
      }
      Class<?> activeView = givenContext.getView().get().getErasedType();
      int i = 0;
      int len = typeViews.length;
      for (; i < len; ++i) {
        if (typeViews[i].isAssignableFrom(activeView)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  private Predicate<? super ModelProperty> hiddenProperties() {
    return (Predicate<ModelProperty>) input -> !input.isHidden();
  }

  private Optional<ResolvedField> findField(
      ResolvedType resolvedType,
      final String fieldName) {

    return StreamSupport.stream(
        fields.in(resolvedType).spliterator(),
        false)
        .filter(input -> fieldName.equals(input.getName())).findFirst();
  }

  private ModelProperty fieldModelProperty(
      ResolvedField childField,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext,
      String namePrefix) {

    String fieldName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    FieldModelProperty fieldModelProperty =
        new FieldModelProperty(
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
        new ModelPropertyContext(
            propertyBuilder,
            new PropertySpecificationBuilder(),
            childField.getRawMember(),
            typeResolver,
            modelContext.getDocumentationType()))
        .updateModelRef(modelRefFactory(
            modelContext,
            enumTypeDeterminer,
            typeNameExtractor));
  }

  private PropertySpecification fieldModelPropertySpecification(
      ResolvedField childField,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext,
      String namePrefix) {

    String fieldName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    FieldModelProperty fieldModelProperty =
        new FieldModelProperty(
            fieldName,
            childField,
            typeResolver,
            modelContext.getAlternateTypeProvider(),
            jacksonProperty);

    PropertySpecificationBuilder propertyBuilder = new PropertySpecificationBuilder()
        .withName(fieldModelProperty.getName())
        .withType(new ModelSpecificationBuilder(
            String.format("%s_%s", modelContext.getParameterId(), "String"))
                      .withReference(new ReferenceModelSpecification(
                          new ModelKey(
                              safeGetPackageName(fieldModelProperty.getType()),
                              typeNameExtractor.typeName(modelContext))))
                      .build())
        .withPosition(fieldModelProperty.position())
        .withRequired(fieldModelProperty.isRequired())
        .withDescription(fieldModelProperty.propertyDescription())
//        .withFacets(fieldModelProperty.allowableValues()) //TODO: Allowable values
        .withExample(fieldModelProperty.example());
    return schemaPluginsManager.propertySpecification(
        new ModelPropertyContext(
            new ModelPropertyBuilder(),
            propertyBuilder,
            childField.getRawMember(),
            typeResolver,
            modelContext.getDocumentationType()));
  }

  private ModelProperty beanModelProperty(
      ResolvedMethod childProperty,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext,
      String namePrefix) {

    String propertyName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    BeanModelProperty beanModelProperty
        = new BeanModelProperty(
        propertyName,
        childProperty,
        typeResolver,
        modelContext.getAlternateTypeProvider(),
        jacksonProperty);

    LOG.debug(
        "Adding property {} to model",
        propertyName);
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
        new ModelPropertyContext(
            propertyBuilder,
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType(),
            new PropertySpecificationBuilder()))
        .updateModelRef(modelRefFactory(
            modelContext,
            enumTypeDeterminer,
            typeNameExtractor));
  }

  private PropertySpecification beanModelPropertySpecification(
      ResolvedMethod childProperty,
      BeanPropertyDefinition jacksonProperty,
      ModelContext modelContext,
      String namePrefix) {

    String propertyName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    BeanModelProperty beanModelProperty
        = new BeanModelProperty(
        propertyName,
        childProperty,
        typeResolver,
        modelContext.getAlternateTypeProvider(),
        jacksonProperty);

    LOG.debug(
        "Adding property {} to model",
        propertyName);
    PropertySpecificationBuilder propertyBuilder = new PropertySpecificationBuilder()
        .withName(beanModelProperty.getName())
        .withType(new ModelSpecificationBuilder(
            String.format("%s_%s", modelContext.getParameterId(), "String"))
                      .withReference(new ReferenceModelSpecification(
                          new ModelKey(
                              safeGetPackageName(beanModelProperty.getType()),
                              typeNameExtractor.typeName(modelContext))))
                      .build())
        .withPosition(beanModelProperty.position())
        .withRequired(beanModelProperty.isRequired())
        .withIsHidden(false)
        .withDescription(beanModelProperty.propertyDescription())
        //TODO: facets
//        .withFacets(new ArrayList<>(
//            new EnumerationFacet<>(beanModelProperty.allowableValues())
//        ))
        .withExample(beanModelProperty.example());
    return schemaPluginsManager.propertySpecification(
        new ModelPropertyContext(
            new ModelPropertyBuilder(),
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType(),
            propertyBuilder));
  }

  private String safeGetPackageName(ResolvedType type) {
    if (type != null
        && type.getErasedType() != null
        && type.getErasedType().getPackage() != null) {
      return type.getErasedType()
                 .getPackage()
                 .getName();
    } else {
      return "";
    }
  }

  private ModelProperty paramModelProperty(
      ResolvedParameterizedMember constructor,
      BeanPropertyDefinition jacksonProperty,
      AnnotatedParameter parameter,
      ModelContext modelContext,
      String namePrefix) {

    String propertyName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    ParameterModelProperty parameterModelProperty
        = new ParameterModelProperty(
        propertyName,
        parameter,
        constructor,
        typeResolver,
        modelContext.getAlternateTypeProvider(),
        jacksonProperty);

    LOG.debug(
        "Adding property {} to model",
        propertyName);
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
        new ModelPropertyContext(
            propertyBuilder,
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType(),
            new PropertySpecificationBuilder()))
        .updateModelRef(modelRefFactory(
            modelContext,
            enumTypeDeterminer,
            typeNameExtractor));
  }


  private PropertySpecification paramModelPropertySpecification(
      ResolvedParameterizedMember constructor,
      BeanPropertyDefinition jacksonProperty,
      AnnotatedParameter parameter,
      ModelContext modelContext,
      String namePrefix) {

    String propertyName = name(
        jacksonProperty,
        modelContext.isReturnType(),
        namingStrategy,
        namePrefix);

    ParameterModelProperty parameterModelProperty
        = new ParameterModelProperty(
        propertyName,
        parameter,
        constructor,
        typeResolver,
        modelContext.getAlternateTypeProvider(),
        jacksonProperty);

    LOG.debug(
        "Adding property {} to model",
        propertyName);
    PropertySpecificationBuilder propertyBuilder = new PropertySpecificationBuilder()
        .withName(parameterModelProperty.getName())
        .withType(new ModelSpecificationBuilder(
            String.format("%s_%s", modelContext.getParameterId(), "String"))
                      .withReference(new ReferenceModelSpecification(
                          new ModelKey(
                              safeGetPackageName(parameterModelProperty.getType()),
                              typeNameExtractor.typeName(modelContext))))
                      .build())
        .withPosition(parameterModelProperty.position())
        .withRequired(parameterModelProperty.isRequired())
        .withIsHidden(false)
        .withDescription(parameterModelProperty.propertyDescription())
//        .withFacets(parameterModelProperty.allowableValues())
        .withExample(parameterModelProperty.example());
    return schemaPluginsManager.propertySpecification(
        new ModelPropertyContext(
            new ModelPropertyBuilder(),
            jacksonProperty,
            typeResolver,
            modelContext.getDocumentationType(),
            propertyBuilder));
  }

  private Optional<ResolvedMethod> findAccessorMethod(
      ResolvedType resolvedType,
      final AnnotatedMember member) {
    return accessors.in(resolvedType).stream()
        .filter(accessorMethod -> {
          SimpleMethodSignatureEquality methodComparer = new SimpleMethodSignatureEquality();
          return methodComparer.test(
              accessorMethod.getRawMember(),
              (Method) member.getMember());
        }).findFirst();
  }

  private List<ModelProperty> fromFactoryMethod(
      final ResolvedType resolvedType,
      final BeanPropertyDefinition beanProperty,
      final AnnotatedParameter member,
      final ModelContext givenContext,
      final String namePrefix) {

    Optional<ModelProperty> property = factoryMethods.in(
        resolvedType,
        factoryMethodOf(member))
        .map((Function<ResolvedParameterizedMember, ModelProperty>) input ->
            paramModelProperty(
                input,
                beanProperty,
                member,
                givenContext,
                namePrefix));
    return property
        .map(Collections::singletonList)
        .orElseGet(ArrayList::new);
  }

  private List<PropertySpecification> specificationFromFactoryMethod(
      final ResolvedType resolvedType,
      final BeanPropertyDefinition beanProperty,
      final AnnotatedParameter member,
      final ModelContext givenContext,
      final String namePrefix) {

    Optional<PropertySpecification> property = factoryMethods.in(
        resolvedType,
        factoryMethodOf(member))
        .map((Function<ResolvedParameterizedMember, PropertySpecification>) input ->
            paramModelPropertySpecification(
                input,
                beanProperty,
                member,
                givenContext,
                namePrefix));
    return property
        .map(Collections::singletonList)
        .orElseGet(ArrayList::new);
  }

  private BeanDescription beanDescription(
      ResolvedType type,
      ModelContext context) {
    if (context.isReturnType()) {
      SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
      return serializationConfig.introspect(serializationConfig.constructType(type.getErasedType()));
    } else {
      DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
      return serializationConfig.introspect(serializationConfig.constructType(type.getErasedType()));
    }
  }
}