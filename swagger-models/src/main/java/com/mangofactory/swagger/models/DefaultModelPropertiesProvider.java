package com.mangofactory.swagger.models;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Function;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.uniqueIndex;

@Component
@Lazy
//@DependsOn("springsMessageConverterObjectMapper")
public class DefaultModelPropertiesProvider implements ModelPropertiesProvider {

  private static final Logger log = LoggerFactory.getLogger(DefaultModelPropertiesProvider.class);
  private ObjectMapper objectMapper;
  private final AlternateTypeProvider alternateTypeProvider;
  private final AccessorsProvider accessors;
  private final FieldsProvider fields;


  @Autowired
  public DefaultModelPropertiesProvider(AlternateTypeProvider alternateTypeProvider, AccessorsProvider accessors,
                                        FieldsProvider fields) {
    this.alternateTypeProvider = alternateTypeProvider;
    this.accessors = accessors;
    this.fields = fields;
  }

  /**
   * Autowire as a setter - construction injection can lead to:
   * org.springframework.beans.factory.BeanCurrentlyInCreationException
   * @param objectMapper
   */
  @Autowired
  public void setObjectMapper(@Qualifier("springsMessageConverterObjectMapper") ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public List<? extends ModelProperty> serializableProperties(ResolvedType resolvedType) {
    List<ModelProperty> serializationCandidates = newArrayList();
    SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
    BeanDescription beanDescription = serializationConfig.introspect(TypeFactory.defaultInstance()
            .constructType(resolvedType.getErasedType()));
    Map<String, BeanPropertyDefinition> propertyLookup = uniqueIndex(beanDescription.findProperties(),
            beanPropertyByInternalName());
    for (BeanModelProperty childProperty : accessors.in(resolvedType)) {
      if (propertyLookup.containsKey(childProperty.getName())) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(childProperty.getName());
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (childProperty.accessorMemberIs((methodName(member)))) {
          serializationCandidates.add(childProperty);
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
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (memberIsAField(member)) {
          serializationCandidates.add(new FieldModelProperty(propertyDefinition.getName(), childField,
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
    for (BeanModelProperty childProperty : accessors.in(resolvedType)) {

      if (propertyLookup.containsKey(childProperty.getName())) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(childProperty.getName());
        try {
          AnnotatedMember member = propertyDefinition.getPrimaryMember();
          if (childProperty.accessorMemberIs(methodName(member))) {
            serializationCandidates.add(childProperty);
          }
        } catch(Exception e) {
          log.warn(e.getMessage());
        }
      }
    }
    return serializationCandidates;
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
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        if (memberIsAField(member)) {
          serializationCandidates.add(new FieldModelProperty(propertyDefinition.getName(), childField,
                  alternateTypeProvider));
        }
      }
    }
    return serializationCandidates;
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

