package com.mangofactory.documentation.schema.property.bean;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.mangofactory.documentation.builders.ModelPropertyBuilder;
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.schema.configuration.ObjectMapperConfigured;
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager;
import com.mangofactory.documentation.schema.property.BeanPropertyDefinitions;
import com.mangofactory.documentation.schema.property.BeanPropertyNamingStrategy;
import com.mangofactory.documentation.schema.property.provider.ModelPropertiesProvider;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.schema.Annotations.*;
import static com.mangofactory.documentation.schema.Collections.*;
import static com.mangofactory.documentation.schema.Maps.*;
import static com.mangofactory.documentation.schema.property.BeanPropertyDefinitions.*;
import static com.mangofactory.documentation.schema.property.bean.Accessors.*;
import static com.mangofactory.documentation.schema.property.bean.BeanModelProperty.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

@Component
public class BeanModelPropertyProvider implements ModelPropertiesProvider {

  private final AccessorsProvider accessors;
  private final BeanPropertyNamingStrategy namingStrategy;
  private ObjectMapper objectMapper;
  private final TypeResolver typeResolver;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public BeanModelPropertyProvider(
          AccessorsProvider accessors,
          TypeResolver typeResolver,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager,
          TypeNameExtractor typeNameExtractor) {

    this.typeResolver = typeResolver;
    this.accessors = accessors;
    this.namingStrategy = namingStrategy;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    this.objectMapper = event.getObjectMapper();
  }

  @VisibleForTesting
  List<ModelProperty> addCandidateProperties(AnnotatedMember member,
      ResolvedMethod childProperty,
      Optional<BeanPropertyDefinition> jacksonProperty,
      ModelContext givenContext) {

    if (member instanceof AnnotatedMethod && memberIsUnwrapped(member)) {
      if (isGetter(((AnnotatedMethod) member).getMember())) {
        return propertiesFor(childProperty.getReturnType(), fromParent(givenContext, childProperty.getReturnType()));
      } else {
        return propertiesFor(childProperty.getArgumentType(0),
                fromParent(givenContext, childProperty.getArgumentType(0)));
      }
    } else {
      return newArrayList(beanModelProperty(childProperty, jacksonProperty, givenContext));
    }
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

  @Override
  public List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext) {

    List<ModelProperty> serializationCandidates = newArrayList();
    BeanDescription beanDescription = beanDescription(type, givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            BeanPropertyDefinitions.beanPropertyByInternalName());
    for (Map.Entry<String, BeanPropertyDefinition> each : propertyLookup.entrySet()) {

      BeanPropertyDefinition propertyDefinition = each.getValue();
      Optional<BeanPropertyDefinition> jacksonProperty
              = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
      AnnotatedMember member = propertyDefinition.getPrimaryMember();
      Optional<ResolvedMethod> accessor = findAccessorMethod(type, each.getKey(), member);
      if (accessor.isPresent()) {
        serializationCandidates
                .addAll(addCandidateProperties(member, accessor.get(), jacksonProperty, givenContext));
      }
    }
    return serializationCandidates;
  }

  private Optional<ResolvedMethod> findAccessorMethod(ResolvedType resolvedType,
                                                      final String propertyName,
                                                      final AnnotatedMember member) {
    return Iterables.tryFind(accessors.in(resolvedType), new Predicate<ResolvedMethod>() {
      public boolean apply(ResolvedMethod accessorMethod) {
        return accessorMemberIs(accessorMethod, memberName(member))
                && propertyName.equals(propertyName(accessorMethod.getRawMember()));
      }
    });
  }

  private ModelProperty beanModelProperty(ResolvedMethod childProperty, Optional<BeanPropertyDefinition>
          jacksonProperty, ModelContext modelContext) {

    BeanPropertyDefinition beanPropertyDefinition = jacksonProperty.get();
    String propertyName = name(beanPropertyDefinition, modelContext.isReturnType(), namingStrategy);
    BeanModelProperty beanModelProperty
            = new BeanModelProperty(propertyName, childProperty, isGetter(childProperty.getRawMember()),
            typeResolver, modelContext.getAlternateTypeProvider());
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
            .name(beanModelProperty.getName())
            .type(beanModelProperty.getType())
            .qualifiedType(beanModelProperty.qualifiedTypeName())
            .position(beanModelProperty.position())
            .required(beanModelProperty.isRequired())
            .isHidden(false)
            .description(beanModelProperty.propertyDescription())
            .allowableValues(beanModelProperty.allowableValues())
            .modelRef(modelRef(beanModelProperty.getType(), fromParent(modelContext, beanModelProperty.getType())));
    return schemaPluginsManager.property(
            new ModelPropertyContext(propertyBuilder, beanPropertyDefinition, modelContext.getDocumentationType()));
  }

  private ModelRef modelRef(ResolvedType type, ModelContext modelContext) {
    if (isContainerType(type)) {
      ResolvedType collectionElementType = collectionElementType(type);
      String elementTypeName = typeNameExtractor.typeName(fromParent(modelContext, collectionElementType));
      return new ModelRef(containerType(type), elementTypeName);
    }
    if (isMapType(type)) {
      String elementTypeName = typeNameExtractor.typeName(fromParent(modelContext, mapValueType(type)));
      return new ModelRef("Map", elementTypeName, true);
    }
    String typeName = typeNameExtractor.typeName(modelContext);
    return new ModelRef(typeName);
  }
}
