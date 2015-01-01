package com.mangofactory.springmvc.plugin;

import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PluginsManager {
  private final Defaults defaults;

  @Autowired
  @Qualifier("documentationPluginRegistry")
  private PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;

  @Autowired
  public PluginsManager(Defaults defaults) {
    this.defaults = defaults;
  }

  public List<DocumentationPlugin> getDocumentationPluginsFor(DocumentationType type) {
    List<DocumentationPlugin> plugins = documentationPlugins.getPluginsFor(type);
    if (plugins.isEmpty()) {
      plugins.add(defaultDocumentationPluginFor(type));
    }
    return plugins;
  }

  private DocumentationPlugin defaultDocumentationPluginFor(DocumentationType type) {
    return new SwaggerSpringMvcPlugin(defaults).build();
  }

  private UnsupportedOperationException orThrow(DocumentationType type) {
    return new UnsupportedOperationException(String.format("Plugin of type %s version %s was not found", type
            .getName(), type.getVersion()));
  }
}
