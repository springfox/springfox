package com.mangofactory.documentation.spi.service;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import org.springframework.plugin.core.Plugin;

public interface OperationBuilderPlugin extends Plugin<DocumentationType> {
  void apply(OperationContext context);
}
