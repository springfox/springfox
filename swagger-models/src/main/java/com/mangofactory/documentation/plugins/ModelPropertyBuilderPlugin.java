package com.mangofactory.documentation.plugins;

import org.springframework.plugin.core.Plugin;

public interface ModelPropertyBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelPropertyContext context);
}
