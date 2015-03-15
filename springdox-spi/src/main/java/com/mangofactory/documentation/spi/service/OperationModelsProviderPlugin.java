package com.mangofactory.documentation.spi.service;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import org.springframework.plugin.core.Plugin;

public interface OperationModelsProviderPlugin extends Plugin<DocumentationType> {
  void apply(RequestMappingContext context);
}
