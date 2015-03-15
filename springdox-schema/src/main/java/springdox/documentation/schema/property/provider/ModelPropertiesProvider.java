package springdox.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.context.ApplicationListener;
import springdox.documentation.schema.ModelProperty;
import springdox.documentation.schema.configuration.ObjectMapperConfigured;
import springdox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;

public interface ModelPropertiesProvider extends ApplicationListener<ObjectMapperConfigured> {
  List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);
}
