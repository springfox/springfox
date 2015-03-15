package springdox.documentation.spi.service;

import org.springframework.plugin.core.Plugin;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.DocumentationContext;
import springdox.documentation.spi.service.contexts.DocumentationContextBuilder;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  boolean isEnabled();

  DocumentationType getDocumentationType();

  DocumentationContext configure(DocumentationContextBuilder builder);
}

