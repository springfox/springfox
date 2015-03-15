package springdox.documentation.spi.schema;


import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.contexts.ModelContext;

public interface ModelBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelContext context);
}
