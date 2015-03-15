package springdox.documentation.spi.schema;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.contexts.ModelPropertyContext;

public interface ModelPropertyBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelPropertyContext context);
}
