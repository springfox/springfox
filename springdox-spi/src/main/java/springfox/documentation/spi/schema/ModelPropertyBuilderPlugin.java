package springfox.documentation.spi.schema;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

public interface ModelPropertyBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelPropertyContext context);
}
