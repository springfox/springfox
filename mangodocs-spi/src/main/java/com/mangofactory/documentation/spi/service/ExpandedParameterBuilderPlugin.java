package com.mangofactory.documentation.spi.service;

import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.contexts.ParameterExpansionContext;
import org.springframework.plugin.core.Plugin;

public interface ExpandedParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterExpansionContext context);
}
