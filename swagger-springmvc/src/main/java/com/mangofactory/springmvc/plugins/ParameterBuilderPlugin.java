package com.mangofactory.springmvc.plugins;

import com.mangofactory.documentation.plugins.DocumentationType;
import org.springframework.plugin.core.Plugin;

public interface ParameterBuilderPlugin extends Plugin<DocumentationType> {
  public void apply(ParameterContext parameterContext);
}
