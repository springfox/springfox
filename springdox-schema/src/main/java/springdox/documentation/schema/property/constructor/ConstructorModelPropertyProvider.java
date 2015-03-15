package springdox.documentation.schema.property.constructor;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springdox.documentation.schema.TypeNameExtractor;
import springdox.documentation.schema.plugins.SchemaPluginsManager;
import springdox.documentation.schema.property.BeanPropertyNamingStrategy;
import springdox.documentation.schema.property.field.FieldModelPropertyProvider;
import springdox.documentation.schema.property.field.FieldProvider;
import springdox.documentation.schema.property.provider.ModelPropertiesProvider;

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
