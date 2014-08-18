package com.mangofactory.swagger.models.property.constructor;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.mangofactory.swagger.models.BeanPropertyNamingStrategy;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider;
import com.mangofactory.swagger.models.property.field.FieldProvider;
import com.mangofactory.swagger.models.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class ConstructorModelPropertyProvider extends FieldModelPropertyProvider implements ModelPropertiesProvider {

  @Autowired
  public ConstructorModelPropertyProvider(FieldProvider fieldProvider, AlternateTypeProvider alternateTypeProvider,
      BeanPropertyNamingStrategy namingStrategy) {

    super(fieldProvider, alternateTypeProvider, namingStrategy);
  }

  @Override
  protected boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Constructor.class.isAssignableFrom(member.getMember().getClass());
  }
}
