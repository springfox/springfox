package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.Accessors.*;
import static com.mangofactory.swagger.models.BeanModelProperty.*;

@Component
public class DefaultModelPropertiesProvider implements ModelPropertiesProvider {

  private static final Logger log = LoggerFactory.getLogger(DefaultModelPropertiesProvider.class);
  private ObjectMapper objectMapper;
  private final TypeResolver typeResolver;
  private final AlternateTypeProvider alternateTypeProvider;
  private final AccessorsProvider accessors;
  private final FieldsProvider fields;

  @Autowired
  public DefaultModelPropertiesProvider(TypeResolver typeResolver, AlternateTypeProvider alternateTypeProvider,
      AccessorsProvider accessors, FieldsProvider fields) {
    this.typeResolver = typeResolver;
    this.alternateTypeProvider = alternateTypeProvider;
    this.accessors = accessors;
    this.fields = fields;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<? extends ModelProperty> serializableProperties(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {
      if (propertyLookup.containsKey(propertyName(childProperty.getName()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getName()));
        Optional<BeanPropertyDefinition> jacksonProperty = jacksonPropertyWithSameInternalName(beanDescription,
                propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (accessorMemberIs(childProperty, methodName(member))) {
          serializationCandidates.add(beanModelProperty(childProperty, jacksonProperty));
        }
      }
    }
    return serializationCandidates;
  }

  private Optional<BeanPropertyDefinition> jacksonPropertyWithSameInternalName(BeanDescription beanDescription,
      BeanPropertyDefinition propertyDefinition) {
    return FluentIterable.from(beanDescription
            .findProperties()).firstMatch(withSameInternalName(propertyDefinition));
  }

  private String methodName(AnnotatedMember member) {
    if (member == null || member.getMember() == null) {
      return "";
    }
    return member.getMember().getName();
  }

  private List<? extends ModelProperty> serializableFields(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            beanPropertyByInternalName());
    for (ResolvedField childField : fields.in(resolvedType)) {
      if (propertyLookup.containsKey(childField.getName())) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(childField.getName());
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (memberIsAField(member)) {
          serializationCandidates.add(new FieldModelProperty(jacksonProperty.get().getName(), childField,
                  alternateTypeProvider));
        }
      }
    }
    return serializationCandidates;
  }

  private boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Field.class.isAssignableFrom(member.getMember().getClass());
  }


  public List<? extends ModelProperty> deserializableProperties(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            beanPropertyByInternalName());
    for (ResolvedMethod childProperty : accessors.in(resolvedType)) {

      if (propertyLookup.containsKey(propertyName(childProperty.getName()))) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(propertyName(childProperty.getName()));
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        try {
          AnnotatedMember member = propertyDefinition.getPrimaryMember();
          if (accessorMemberIs(childProperty, methodName(member))) {
            serializationCandidates.add(beanModelProperty(childProperty, jacksonProperty));
          }
        } catch (Exception e) {
          log.warn(e.getMessage());
        }
      }
    }
    return serializationCandidates;
  }

  private BeanModelProperty beanModelProperty(ResolvedMethod childProperty, Optional<BeanPropertyDefinition>
          jacksonProperty) {
    return new BeanModelProperty(jacksonProperty.get().getName(),
            childProperty, isGetter(childProperty.getRawMember()), typeResolver, alternateTypeProvider);
  }

  public List<? extends ModelProperty> deserializableFields(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    DeserializationConfig serializationConfig = objectMapper.getDeserializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            beanPropertyByInternalName());
    for (ResolvedField childField : fields.in(resolvedType)) {
      if (propertyLookup.containsKey(childField.getName())) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(childField.getName());
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (memberIsAField(member)) {
          serializationCandidates.add(new FieldModelProperty(jacksonProperty.get().getName(), childField,
                  alternateTypeProvider));
        }
      }
    }
    return serializationCandidates;
  }

  private Predicate<BeanPropertyDefinition> withSameInternalName(final BeanPropertyDefinition propertyDefinition) {
    return new Predicate<BeanPropertyDefinition>() {
      @Override
      public boolean apply(BeanPropertyDefinition input) {
        return input.getInternalName() == propertyDefinition.getInternalName();
      }
    };
  }

  private Function<BeanPropertyDefinition, String> beanPropertyByInternalName() {
    return new Function<BeanPropertyDefinition, String>() {
      @Override
      public String apply(BeanPropertyDefinition input) {
        return input.getInternalName();
      }
    };
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType type) {
    ArrayList<ModelProperty> modelProperties = newArrayList(serializableFields(type));
    modelProperties.addAll(serializableProperties(type));
    return modelProperties;
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type) {
    ArrayList<ModelProperty> modelProperties = newArrayList(deserializableFields(type));
    modelProperties.addAll(deserializableProperties(type));
    return modelProperties;
  }
}

