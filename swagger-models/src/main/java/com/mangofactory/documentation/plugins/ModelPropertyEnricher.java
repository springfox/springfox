package com.mangofactory.documentation.plugins;

import org.springframework.plugin.core.Plugin;

public interface ModelPropertyEnricher extends Plugin<DocumentationType> {
  void enrich(ModelPropertyContext context);
}
