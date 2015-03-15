package com.mangofactory.documentation.schema.property.constructor;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.mangofactory.documentation.schema.TypeNameExtractor;
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager;
import com.mangofactory.documentation.schema.property.BeanPropertyNamingStrategy;
import com.mangofactory.documentation.schema.property.field.FieldModelPropertyProvider;
import com.mangofactory.documentation.schema.property.field.FieldProvider;
import com.mangofactory.documentation.schema.property.provider.ModelPropertiesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class ConstructorModelPropertyProvider extends FieldModelPropertyProvider implements ModelPropertiesProvider {

  @Autowired
  public ConstructorModelPropertyProvider(
          FieldProvider fieldProvider,
          BeanPropertyNamingStrategy namingStrategy,
          SchemaPluginsManager schemaPluginsManager,
          TypeNameExtractor extractor) {

    super(fieldProvider, namingStrategy, schemaPluginsManager, extractor);
  }

  @Override
  protected boolean memberIsAField(AnnotatedMember member) {
    return member != null
            && member.getMember() != null
            && Constructor.class.isAssignableFrom(member.getMember().getClass());
  }
}
