package com.mangofactory.documentation.schema.property.field;

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
import com.mangofactory.documentation.schema.ModelProperty;
import com.mangofactory.documentation.schema.ModelRef;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager;
import com.mangofactory.documentation.schema.property.BeanPropertyDefinitions;
import com.mangofactory.documentation.schema.property.BeanPropertyNamingStrategy;
import com.mangofactory.documentation.schema.property.provider.ModelPropertiesProvider;
import com.mangofactory.documentation.builders.ModelPropertyBuilder;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.documentation.schema.Annotations.*;
import static com.mangofactory.documentation.schema.Collections.*;
import static com.mangofactory.documentation.schema.property.BeanPropertyDefinitions.*;
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*;

@Component
public class FieldModelPropertyProvider implements ModelPropertiesProvider {

  private final FieldProvider fieldProvider;
  private final BeanPropertyNamingStrategy namingStrategy;
  private ObjectMapper objectMapper;
  private final SchemaPluginsManager schemaPluginsManager;
  private final TypeNameExtractor typeNameExtractor;

  @Autowired
  public FieldModelPropertyProvider(
          FieldProvider fieldProvider,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager,
          TypeNameExtractor typeNameExtractor) {

    this.fieldProvider = fieldProvider;
    this.namingStrategy = namingStrategy;
    this.schemaPluginsManager = schemaPluginsManager;
    this.typeNameExtractor = typeNameExtractor;
  }

  @VisibleForTesting
  List<ModelProperty> addSerializationCandidates(AnnotatedMember member, ResolvedField
          childField, Optional<BeanPropertyDefinition> jacksonProperty, ModelContext givenContext) {
    if (memberIsAField(member)) {
      if (memberIsUnwrapped(member)) {
        return propertiesFor(childField.getType(), fromParent(givenContext, childField.getType()));
      } else {
        String fieldName = name(jacksonProperty.get(), true, namingStrategy);
        return newArrayList(modelPropertyFrom(childField, fieldName, givenContext));
      }
    }
    return newArrayList();
  }

  private ModelProperty modelPropertyFrom(ResolvedField childField, String fieldName,
      ModelContext modelContext) {
    FieldModelProperty fieldModelProperty = new FieldModelProperty(fieldName, childField, modelContext
            .getAlternateTypeProvider());
    String typeName = typeNameExtractor.typeName(fromParent(modelContext, childField.getType()));
    ModelPropertyBuilder propertyBuilder = new ModelPropertyBuilder()
            .name(fieldModelProperty.getName())
            .type(childField.getType())
            .typeName(typeName)
            .qualifiedType(fieldModelProperty.qualifiedTypeName())
            .position(fieldModelProperty.position())
            .required(fieldModelProperty.isRequired())
            .description(fieldModelProperty.propertyDescription())
            .allowableValues(fieldModelProperty.allowableValues())
            .items(itemModelRef(fieldModelProperty.getType(), modelContext));
    return schemaPluginsManager.property(new ModelPropertyContext(propertyBuilder,
            childField.getRawMember(), modelContext.getDocumentationType()));
  }

  @Override
  public List<ModelProperty> propertiesFor(ResolvedType type,
      ModelContext givenContext) {

    List<ModelProperty> serializationCandidates = newArrayList();
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
  private ModelRef itemModelRef(ResolvedType type, ModelContext modelContext) {
    if (!isContainerType(type)) {
      return null;
    }
    ResolvedType collectionElementType = collectionElementType(type);
    String elementTypeName =  typeNameExtractor.typeName(fromParent(modelContext, collectionElementType));

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
