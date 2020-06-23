package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.ModelSpecificationRegistry;

public interface ModelNamesRegistryFactoryPlugin extends Plugin<DocumentationType> {
  ModelNamesRegistry modelNamesRegistry(ModelSpecificationRegistry registry);
}
