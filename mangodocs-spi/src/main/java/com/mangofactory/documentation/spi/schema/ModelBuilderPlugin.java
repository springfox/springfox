package com.mangofactory.documentation.spi.schema;


import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface ModelBuilderPlugin extends Plugin<DocumentationType> {
  void apply(ModelContext context);
}
