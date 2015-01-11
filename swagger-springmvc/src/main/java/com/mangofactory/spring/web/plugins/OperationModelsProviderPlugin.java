package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.spring.web.scanners.RequestMappingContext;
import org.springframework.plugin.core.Plugin;

public interface OperationModelsProviderPlugin extends Plugin<DocumentationType> {
  public void apply(RequestMappingContext context);
}
