package com.mangofactory.springmvc.plugin;

import org.springframework.plugin.core.Plugin;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  boolean isEnabled();

  String getName();

  DocumentationContext build(DocumentationContextBuilder builder);
}

