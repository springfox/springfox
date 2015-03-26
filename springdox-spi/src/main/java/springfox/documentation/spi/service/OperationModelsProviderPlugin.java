package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.RequestMappingContext;

public interface OperationModelsProviderPlugin extends Plugin<DocumentationType> {
  void apply(RequestMappingContext context);
}
