package com.mangofactory.schema.plugins;


import org.springframework.plugin.core.Plugin;

public interface ModelBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelContext context);
}
