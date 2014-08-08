package com.mangofactory.swagger.models.property.constructor;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.ModelProperty;
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider;
import com.mangofactory.swagger.models.property.field.FieldProvider;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

/**
 * @author fgaule
 * @since 17/07/2014
 */
@Component
public class ConstructorModelPropertyProvider extends FieldModelPropertyProvider implements ModelPropertiesProvider{

  @Autowired
  public ConstructorModelPropertyProvider(@Qualifier("swaggerObjectMapper") ObjectMapper objectMapper,
                                          FieldProvider fieldProvider, AlternateTypeProvider alternateTypeProvider) {
    super(objectMapper, fieldProvider, alternateTypeProvider);
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForSerialization(ResolvedType type) {
    return super.propertiesForSerialization(type);
  }

  @Override
  public Iterable<? extends ModelProperty> propertiesForDeserialization(ResolvedType type) {
    return super.propertiesForDeserialization(type);
  }

  @Override
  protected boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Constructor.class.isAssignableFrom(member.getMember().getClass());
  }
}
