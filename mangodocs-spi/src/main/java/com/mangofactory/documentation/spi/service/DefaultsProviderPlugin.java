package com.mangofactory.documentation.spi.service;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import org.springframework.plugin.core.Plugin;

public interface DefaultsProviderPlugin extends Plugin<DocumentationType> {
  DocumentationContextBuilder create(DocumentationType documentationType);
}
