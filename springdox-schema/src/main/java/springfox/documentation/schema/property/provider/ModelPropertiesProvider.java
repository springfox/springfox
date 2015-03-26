package springfox.documentation.schema.property.provider;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.context.ApplicationListener;
import springfox.documentation.schema.ModelProperty;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.List;

public interface ModelPropertiesProvider extends ApplicationListener<ObjectMapperConfigured> {
  List<ModelProperty> propertiesFor(ResolvedType type, ModelContext givenContext);
}
