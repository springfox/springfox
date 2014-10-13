package com.mangofactory.swagger.models.property.provider;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.jacksonPropertyWithSameInternalName;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.BeanPropertyDefinitions;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.ResolvedMemberProvider;

public abstract class AbstractModelPropertyProvider<T extends ResolvedMember> implements ModelPropertiesProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractModelPropertyProvider.class);

  protected final ResolvedMemberProvider<T> resolvedMemberProvider;
  protected final BeanPropertyNamingStrategy namingStrategy;
  protected final AlternateTypeProvider alternateTypeProvider;
  protected ObjectMapper objectMapper;

  protected AbstractModelPropertyProvider(ResolvedMemberProvider<T> resolvedMemberProvider,
      AlternateTypeProvider alternateTypeProvider, BeanPropertyNamingStrategy namingStrategy) {
    this.resolvedMemberProvider = resolvedMemberProvider;
    this.namingStrategy = namingStrategy;
    this.alternateTypeProvider = alternateTypeProvider;
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType resolvedType) {
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
        .constructType(resolvedType.getErasedType()));

    return propertiesForBeanDescription(resolvedType, beanDescription, true);
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type) {
    DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
    BeanDescription beanDescription = deserializationConfig.introspect(TypeFactory.defaultInstance()
        .constructType(type.getErasedType()));

    return propertiesForBeanDescription(type, beanDescription, false);
  }

  private Iterable<? extends ModelProperty> propertiesForBeanDescription(ResolvedType resolvedType,
      BeanDescription beanDescription, boolean forSerialization) {
    List<ModelProperty> candidates = newArrayList();
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(), 
        BeanPropertyDefinitions.beanPropertyByInternalName());

    for (T resolvedMember : resolvedMemberProvider.in(resolvedType)) {
      final String propertyDefinitionKey = getPropertyDefinitionKey(resolvedMember);
      if (propertyLookup.containsKey(propertyDefinitionKey)) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyDefinitionKey);
        Optional<BeanPropertyDefinition> jacksonProperty = jacksonPropertyWithSameInternalName(
            beanDescription, propertyDefinition);

        try {
          AnnotatedMember annotatedMember = propertyDefinition.getPrimaryMember();
          if (isApplicable(resolvedMember, annotatedMember)) {
            addModelProperties(candidates, resolvedMember, jacksonProperty, forSerialization, annotatedMember);
          }
        } catch (Exception e) {
          LOG.warn(e.getMessage());
        }
      }
    }

    return candidates;
  }

  protected abstract String getPropertyDefinitionKey(T resolvedMember);

  protected abstract boolean isApplicable(T resolvedMember, AnnotatedMember annotatedMember);

  private void addModelProperties(List<ModelProperty> candidates, T resolvedMember, 
      Optional<BeanPropertyDefinition> jacksonProperty, boolean forSerialization, AnnotatedMember annotatedMember) {
    
    if (isUnwrapped(annotatedMember)) {
      final ResolvedType unwrappedType = getUnwrappedType(resolvedMember, forSerialization);
      Iterables.addAll(candidates, forSerialization ? propertiesForSerialization(unwrappedType) : propertiesForDeserialization(unwrappedType));
    } else {
      addModelProperty(candidates, resolvedMember, jacksonProperty, forSerialization);
    }
  }

  private ResolvedType getUnwrappedType(T resolvedMember, boolean forSerialization) {
    return forSerialization ? getUnwrappedTypeForSerialization(resolvedMember) : getUnwrappedTypeForDeserialization(resolvedMember);
  }

  private ResolvedType getUnwrappedTypeForSerialization(T resolvedMember) {
    return resolvedMember.getType();
  }
  
  protected abstract ResolvedType getUnwrappedTypeForDeserialization(T resolvedMember);

  protected abstract void addModelProperty(List<ModelProperty> candidates, T resolvedMember,
      Optional<BeanPropertyDefinition> jacksonProperty, boolean forSerialization);

  private boolean isUnwrapped(AnnotatedMember annotatedMember) {
    return annotatedMember.hasAnnotation(JsonUnwrapped.class) && annotatedMember.getAnnotation(JsonUnwrapped.class).enabled();
  }

  @Override
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.namingStrategy.setObjectMapper(objectMapper);
  }
}
