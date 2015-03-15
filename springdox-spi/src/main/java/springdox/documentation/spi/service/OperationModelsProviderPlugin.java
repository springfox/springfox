package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.RequestMappingContext;

public interface OperationModelsProviderPlugin extends Plugin<DocumentationType> {
  void apply(RequestMappingContext context);
}
