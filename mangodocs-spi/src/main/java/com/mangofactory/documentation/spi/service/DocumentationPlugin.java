package com.mangofactory.documentation.spi.service;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContext;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import org.springframework.plugin.core.Plugin;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  boolean isEnabled();

  DocumentationType getDocumentationType();

  DocumentationContext configure(DocumentationContextBuilder builder);
}

