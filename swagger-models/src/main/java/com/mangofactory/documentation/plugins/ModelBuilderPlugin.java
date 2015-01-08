package com.mangofactory.documentation.plugins;


import com.mangofactory.schema.ModelContext;
import org.springframework.plugin.core.Plugin;

public interface ModelBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelContext context);
}
