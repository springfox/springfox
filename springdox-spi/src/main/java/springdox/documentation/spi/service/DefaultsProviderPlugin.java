package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.DocumentationContextBuilder;

public interface DefaultsProviderPlugin extends Plugin<DocumentationType> {
  DocumentationContextBuilder create(DocumentationType documentationType);
}
