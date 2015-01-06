package com.mangofactory.documentation.plugins;


import com.mangofactory.schema.ModelContext;
import org.springframework.plugin.core.Plugin;

public interface ModelEnricher extends Plugin<DocumentationType> {
  void enrich(ModelContext context);
}
