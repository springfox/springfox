package springfox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;

public interface DefaultsProviderPlugin extends Plugin<DocumentationType> {
  DocumentationContextBuilder create(DocumentationType documentationType);
}
