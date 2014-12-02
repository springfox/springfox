package com.mangofactory.swagger.models.property.bean;

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
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.BeanPropertyDefinitions;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Annotations.*;
import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.*;
import static com.mangofactory.swagger.models.property.bean.Accessors.*;
import static com.mangofactory.swagger.models.property.bean.BeanModelProperty.*;

@Component
public class BeanModelPropertyProvider implements ModelPropertiesProvider {

  private static final Logger LOG = LoggerFactory.getLogger(BeanModelPropertyProvider.class);
  private final AccessorsProvider accessors;
  private final BeanPropertyNamingStrategy namingStrategy;
  private ObjectMapper objectMapper;
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public BeanModelPropertyProvider(
          AccessorsProvider accessors,
          TypeResolver typeResolver,
          AlternateTypeProvider alternateTypeProvider,
          BeanPropertyNamingStrategy namingStrategy) {

    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.accessors = accessors;
    this.namingStrategy = namingStrategy;
  }


  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            BeanPropertyDefinitions.beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {
      if (propertyLookup.containsKey(propertyName(childProperty.getRawMember()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getRawMember()));
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (accessorMemberIs(childProperty, methodName(member))) {
          serializationCandidates
                  .addAll(newArrayList(addSerializationCandidates(member, childProperty, jacksonProperty)));
        }
      }
    }
    return serializationCandidates;
  }

  @VisibleForTesting
  Iterable<? extends ModelProperty> addSerializationCandidates(AnnotatedMember member,
      ResolvedMethod childProperty,
      Optional<BeanPropertyDefinition> jacksonProperty) {

    if (member instanceof AnnotatedMethod && memberIsUnwrapped(member)) {
      Iterable<? extends ModelProperty> properties;
      if (isGetter(((AnnotatedMethod)member).getMember())) {
        properties = propertiesForSerialization(childProperty.getReturnType());
      } else {
        properties = propertiesForSerialization(childProperty.getArgumentType(0));
      }
      return properties;
    } else {
      return newArrayList(beanModelProperty(childProperty, jacksonProperty, true));
    }
  }

  @VisibleForTesting
  Iterable<? extends ModelProperty> addDeserializationCandidates(AnnotatedMember member,
      ResolvedMethod childProperty,
      Optional<BeanPropertyDefinition> jacksonProperty) {

    if (member instanceof AnnotatedMethod && memberIsUnwrapped(member)) {
      Iterable<? extends ModelProperty> properties;
      if (isGetter(((AnnotatedMethod)member).getMember())) {
        properties = propertiesForDeserialization(childProperty.getReturnType());
      } else {
        properties = propertiesForDeserialization(childProperty.getArgumentType(0));
      }
      return properties;
    } else {
      return newArrayList(beanModelProperty(childProperty, jacksonProperty, true));
    }
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            BeanPropertyDefinitions.beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {

      if (propertyLookup.containsKey(propertyName(childProperty.getRawMember()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getRawMember()));
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        try {
          AnnotatedMember member = propertyDefinition.getPrimaryMember();
          if (accessorMemberIs(childProperty, methodName(member))) {
            serializationCandidates
                    .addAll(newArrayList(addDeserializationCandidates(member, childProperty, jacksonProperty)));
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


  private BeanModelProperty beanModelProperty(ResolvedMethod childProperty, Optional<BeanPropertyDefinition>
          jacksonProperty, boolean forSerialization) {

    BeanPropertyDefinition beanPropertyDefinition = jacksonProperty.get();
    String propertyName = name(beanPropertyDefinition, forSerialization, namingStrategy);
    return new BeanModelProperty(propertyName, beanPropertyDefinition, childProperty,
            isGetter(childProperty.getRawMember()),
            typeResolver, alternateTypeProvider);
  }

}
