package com.mangofactory.swagger.models.property.field;

import static com.mangofactory.swagger.models.property.BeanPropertyDefinitions.name;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.base.Optional;
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.provider.AbstractModelPropertyProvider;

@Component
public class FieldModelPropertyProvider extends AbstractModelPropertyProvider<ResolvedField> {

  @Autowired
  public FieldModelPropertyProvider(FieldProvider fieldProvider, AlternateTypeProvider alternateTypeProvider,
      BeanPropertyNamingStrategy namingStrategy) {

    super(fieldProvider, alternateTypeProvider, namingStrategy);
  }

  @Override
  protected String getPropertyDefinitionKey(ResolvedField resolvedField) {
    return resolvedField.getName();
  }
  
  @Override
  protected boolean isApplicable(ResolvedField resolvedField, AnnotatedMember annotatedMember) {
    return memberIsAField(annotatedMember);
  }
  
  @Override
  protected void addModelProperties(List<ModelProperty> candidates, ResolvedField resolvedField,
      AnnotatedMember annotatedMember, Optional<BeanPropertyDefinition> jacksonProperty, boolean forSerialization) {
    String fieldName = name(jacksonProperty.get(), true, namingStrategy);
    candidates.add(new FieldModelProperty(fieldName, resolvedField, alternateTypeProvider));
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
