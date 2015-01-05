package com.mangofactory.schema.property.field;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mangofactory.documentation.plugins.ModelPropertyContext;
import com.mangofactory.documentation.plugins.PluginsManager;
import com.mangofactory.schema.BeanPropertyNamingStrategy;
import com.mangofactory.schema.ModelContext;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.BeanPropertyDefinitions;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import com.mangofactory.service.model.ModelRef;
import com.mangofactory.service.model.builder.ModelPropertyBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.schema.Annotations.*;
import static com.mangofactory.schema.Collections.*;
import static com.mangofactory.schema.ResolvedTypes.*;
import static com.mangofactory.schema.property.BeanPropertyDefinitions.*;

@Component
public class FieldModelPropertyProvider implements ModelPropertiesProvider {

  private final FieldProvider fieldProvider;
  private final AlternateTypeProvider alternateTypeProvider;
  private final BeanPropertyNamingStrategy namingStrategy;
  private ObjectMapper objectMapper;
  private final PluginsManager pluginsManager;

  @Autowired
  public FieldModelPropertyProvider(
          FieldProvider fieldProvider,
          AlternateTypeProvider alternateTypeProvider,
          BeanPropertyNamingStrategy namingStrategy,
          PluginsManager pluginsManager) {

    this.fieldProvider = fieldProvider;
    this.alternateTypeProvider = alternateTypeProvider;
    this.namingStrategy = namingStrategy;
    this.pluginsManager = pluginsManager;
  }

  @VisibleForTesting
  List<com.mangofactory.service.model.ModelProperty> addSerializationCandidates(AnnotatedMember member, ResolvedField
          childField, Optional<BeanPropertyDefinition> jacksonProperty, ModelContext givenContext) {
    if (memberIsAField(member)) {
      if (memberIsUnwrapped(member)) {
        return propertiesFor(childField.getType(), givenContext);
      } else {
        String fieldName = name(jacksonProperty.get(), true, namingStrategy);
        return newArrayList(modelPropertyFrom(childField, fieldName, givenContext));
      }
    }
    return newArrayList();
  }

  private com.mangofactory.service.model.ModelProperty modelPropertyFrom(ResolvedField childField, String fieldName,
                                                                         ModelContext modelContext) {
    FieldModelProperty fieldModelProperty = new FieldModelProperty(fieldName, childField, alternateTypeProvider);
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
            .name(fieldModelProperty.getName())
            .type(childField.getType())
            .qualifiedType(fieldModelProperty.qualifiedTypeName())
            .position(fieldModelProperty.position())
            .required(fieldModelProperty.isRequired())
            .description(fieldModelProperty.propertyDescription())
            .allowableValues(fieldModelProperty.allowableValues())
            .items(itemModelRef(fieldModelProperty.getType()));
    return pluginsManager.enrichProperty(new ModelPropertyContext(propertyBuilder,
            childField.getRawMember(),  modelContext.getDocumentationType()));
  }

  @Override
  public List<com.mangofactory.service.model.ModelProperty> propertiesFor(ResolvedType type,
      ModelContext givenContext) {

    List<com.mangofactory.service.model.ModelProperty> serializationCandidates = newArrayList();
    BeanDescription beanDescription = beanDescription(type, givenContext);
    Map<String, BeanPropertyDefinition> propertyLookup = Maps.uniqueIndex(beanDescription.findProperties(),
            BeanPropertyDefinitions.beanPropertyByInternalName());

    for (ResolvedField childField : fieldProvider.in(type)) {
      if (propertyLookup.containsKey(childField.getName())) {
        BeanPropertyDefinition propertyDefinition = propertyLookup.get(childField.getName());
        Optional<BeanPropertyDefinition> jacksonProperty
                = jacksonPropertyWithSameInternalName(beanDescription, propertyDefinition);
        AnnotatedMember member = propertyDefinition.getPrimaryMember();
        serializationCandidates.addAll(newArrayList(addSerializationCandidates(member, childField, jacksonProperty,
                givenContext)));
      }
    }
    return serializationCandidates;
  }
  private ModelRef itemModelRef(ResolvedType type) {
    if (!isContainerType(type)) {
      return null;
    }
    ResolvedType collectionElementType = collectionElementType(type);
    String elementTypeName = typeName(collectionElementType);

    return new ModelRef(elementTypeName);
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
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  protected boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Field.class.isAssignableFrom(member.getMember().getClass());
  }

}
