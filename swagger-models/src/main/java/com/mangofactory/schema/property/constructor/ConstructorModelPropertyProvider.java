package com.mangofactory.schema.property.constructor;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.mangofactory.schema.plugins.SchemaPluginsManager;
import com.mangofactory.schema.property.BeanPropertyNamingStrategy;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.property.field.FieldModelPropertyProvider;
import com.mangofactory.schema.property.field.FieldProvider;
import com.mangofactory.schema.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class ConstructorModelPropertyProvider extends FieldModelPropertyProvider implements ModelPropertiesProvider {

  @Autowired
  public ConstructorModelPropertyProvider(
          FieldProvider fieldProvider,
          AlternateTypeProvider alternateTypeProvider,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager) {

    super(fieldProvider, alternateTypeProvider, namingStrategy, schemaPluginsManager);
  }

  @Override
  protected boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Constructor.class.isAssignableFrom(member.getMember().getClass());
  }
}
