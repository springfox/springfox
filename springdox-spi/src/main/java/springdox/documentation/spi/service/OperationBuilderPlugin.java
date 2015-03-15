package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.OperationContext;

public interface OperationBuilderPlugin extends Plugin<DocumentationType> {
  void apply(OperationContext context);
}
