package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;

public interface OperationBuilderPlugin extends Plugin<DocumentationType> {
  void apply(OperationContext context);
}
