package com.mangofactory.documentation.spi.schema;

import com.mangofactory.documentation.spi.schema.contexts.ModelPropertyContext;
import com.mangofactory.documentation.spi.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface ModelPropertyBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelPropertyContext context);
}
