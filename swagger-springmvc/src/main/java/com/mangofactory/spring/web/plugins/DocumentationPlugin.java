package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  boolean isEnabled();

  DocumentationType getDocumentationType();

  DocumentationContext build(DocumentationContextBuilder builder);
}

