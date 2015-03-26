package springfox.documentation.spi.schema;


import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;

public interface ModelBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelContext context);
}
