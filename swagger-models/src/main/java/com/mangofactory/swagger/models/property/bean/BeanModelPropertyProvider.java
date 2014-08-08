package com.mangofactory.swagger.models.property.bean;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.PropertyUtils;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Accessors.*;
import static com.mangofactory.swagger.models.property.bean.BeanModelProperty.*;

/**
 * @author fgaule
 * @since 17/07/2014
 */
@Component
public class BeanModelPropertyProvider implements ModelPropertiesProvider {

  private static final Logger LOG = LoggerFactory.getLogger(BeanModelPropertyProvider.class);
  private final AccessorsProvider accessors;
  private ObjectMapper objectMapper;
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;

  @Autowired
  public BeanModelPropertyProvider(@Qualifier("swaggerObjectMapper") ObjectMapper objectMapper,
                                   AccessorsProvider accessors, TypeResolver typeResolver,
                                   AlternateTypeProvider alternateTypeProvider) {
    this.objectMapper = objectMapper;
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.accessors = accessors;
  }


  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            PropertyUtils.beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {
      if (propertyLookup.containsKey(propertyName(childProperty.getName()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getName()));
        Optional<BeanPropertyDefinition> jacksonProperty = PropertyUtils.jacksonPropertyWithSameInternalName
                (beanDescription,
                        propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (accessorMemberIs(childProperty, methodName(member))) {
          serializationCandidates.add(beanModelProperty(childProperty, jacksonProperty));
        }
      }
    }
    return serializationCandidates;
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            PropertyUtils.beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {

      if (propertyLookup.containsKey(propertyName(childProperty.getName()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getName()));
        Optional<BeanPropertyDefinition> jacksonProperty
                = PropertyUtils.jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        try {
          AnnotatedMember member = propertyDefinition.getPrimaryMember();
          if (accessorMemberIs(childProperty, methodName(member))) {
            serializationCandidates.add(beanModelProperty(childProperty, jacksonProperty));
          }
        } catch (Exception e) {
          LOG.warn(e.getMessage());
        }
      }
    }
    return serializationCandidates;
  }

  private String methodName(AnnotatedMember member) {
    if (member == null || member.getMember() == null) {
      return "";
    }
    return member.getMember().getName();
  }


  private BeanModelProperty beanModelProperty(ResolvedMethod childProperty, Optional<BeanPropertyDefinition>
          jacksonProperty) {
    BeanPropertyDefinition beanPropertyDefinition = jacksonProperty.get();
    return new BeanModelProperty(beanPropertyDefinition,
            childProperty, isGetter(childProperty.getRawMember()), typeResolver, alternateTypeProvider);
  }

}
