package com.mangofactory.springmvc.plugin;

import com.mangofactory.service.model.Group;
import org.springframework.plugin.core.Plugin;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

public interface DocumentationPlugin extends Plugin<DocumentationType> {
  boolean isEnabled();

  Group scan(List<RequestMappingHandlerMapping> handlerMappings);

  String getName();
}

