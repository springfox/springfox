package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface ParameterExpanderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterExpansionContext context);
}
