package com.mangofactory.springmvc.plugins;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentationPluginsManager {
  @Autowired
  @Qualifier("documentationPluginRegistry")
  private PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;

  public List<DocumentationPlugin> getDocumentationPluginsFor(DocumentationType type) {
    List<DocumentationPlugin> plugins = documentationPlugins.getPluginsFor(type);
    if (plugins.isEmpty()) {
      plugins.add(defaultDocumentationPluginFor(type));
    }
    return plugins;
  }

  private DocumentationPlugin defaultDocumentationPluginFor(DocumentationType type) {
    return new SwaggerSpringMvcPlugin();
  }
}
