package com.mangofactory.schema.property.bean;

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
import com.mangofactory.schema.plugins.ModelPropertyContext;
import com.mangofactory.schema.plugins.SchemaPluginsManager;
import com.mangofactory.schema.BeanPropertyNamingStrategy;
import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.BeanPropertyDefinitions;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import com.mangofactory.service.model.ModelProperty;
import com.mangofactory.service.model.ModelRef;
import com.mangofactory.service.model.builder.ModelPropertyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.schema.Annotations.*;
import static com.mangofactory.schema.Collections.*;
import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.schema.property.BeanPropertyDefinitions.*;
import static com.mangofactory.schema.property.bean.Accessors.*;
import static com.mangofactory.schema.property.bean.BeanModelProperty.*;

@Component
public class BeanModelPropertyProvider implements ModelPropertiesProvider {

  private static final Logger LOG = LoggerFactory.getLogger(BeanModelPropertyProvider.class);
  private final AccessorsProvider accessors;
  private final BeanPropertyNamingStrategy namingStrategy;
  private ObjectMapper objectMapper;
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final SchemaPluginsManager schemaPluginsManager;

  @Autowired
  public BeanModelPropertyProvider(
          AccessorsProvider accessors,
          TypeResolver typeResolver,
          AlternateTypeProvider alternateTypeProvider,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager) {

    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.accessors = accessors;
    this.namingStrategy = namingStrategy;
    this.schemaPluginsManager = schemaPluginsManager;
  }

  @VisibleForTesting
  List<com.mangofactory.service.model.ModelProperty> addCandidateProperties(AnnotatedMember member,
      ResolvedMethod childProperty,
      Optional<BeanPropertyDefinition> jacksonProperty,
      ModelContext givenContext) {

    if (member instanceof AnnotatedMethod && memberIsUnwrapped(member)) {
      if (isGetter(((AnnotatedMethod)member).getMember())) {
        return propertiesFor(childProperty.getReturnType(), givenContext);
      } else {
        return propertiesFor(childProperty.getArgumentType(0), givenContext);
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
  public List<com.mangofactory.service.model.ModelProperty> propertiesFor(ResolvedType type, ModelContext
          givenContext) {
    List<com.mangofactory.service.model.ModelProperty> serializationCandidates = newArrayList();
    BeanDescription beanDescription = beanDescription(type, givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            BeanPropertyDefinitions.beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(type)) {

      String propertyName = propertyName(childProperty.getRawMember());
      if (propertyLookup.containsKey(propertyName)) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName);
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        try {
          AnnotatedMember member = propertyDefinition.getPrimaryMember();
          if (accessorMemberIs(childProperty, methodName(member))) {
            serializationCandidates
                    .addAll(newArrayList(addCandidateProperties(member, childProperty, jacksonProperty, givenContext)));
          }
        } catch (Exception e) {
          LOG.warn(e.getMessage());
        }
      }
    }
    return serializationCandidates;
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.namingStrategy.setObjectMapper(objectMapper);
  }

  private String methodName(AnnotatedMember member) {
    if (member == null || member.getMember() == null) {
      return "";
    }
    return member.getMember().getName();
  }


  private ModelProperty beanModelProperty(ResolvedMethod childProperty, Optional<BeanPropertyDefinition>
          jacksonProperty, ModelContext modelContext) {

    BeanPropertyDefinition beanPropertyDefinition = jacksonProperty.get();
    String propertyName = name(beanPropertyDefinition, modelContext.isReturnType(), namingStrategy);
    BeanModelProperty beanModelProperty = new BeanModelProperty(propertyName, childProperty,
            isGetter(childProperty.getRawMember()),
            typeResolver, alternateTypeProvider);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
            .name(beanModelProperty.getName())
            .type(beanModelProperty.getType())
            .qualifiedType(beanModelProperty.qualifiedTypeName())
            .position(beanModelProperty.position())
            .required(beanModelProperty.isRequired())
            .description(beanModelProperty.propertyDescription())
            .allowableValues(beanModelProperty.allowableValues())
            .items(itemModelRef(beanModelProperty.getType()));
    return schemaPluginsManager.enrichProperty(
            new ModelPropertyContext(propertyBuilder, beanPropertyDefinition, modelContext.getDocumentationType()));
  }

  private ModelRef itemModelRef(ResolvedType type) {
    if (!isContainerType(type)) {
      return null;
    }
    ResolvedType collectionElementType = collectionElementType(type);
    String elementTypeName = typeName(collectionElementType);

    return new ModelRef(elementTypeName);
  }
}
