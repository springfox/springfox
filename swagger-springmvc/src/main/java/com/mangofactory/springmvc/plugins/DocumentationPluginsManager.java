package com.mangofactory.springmvc.plugins;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.service.model.ApiListing;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentationPluginsManager {

  private final PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;


  private final PluginRegistry<ApiListingEnricher, DocumentationType> apiListingPlugins;

  @Autowired
  public DocumentationPluginsManager(
          @Qualifier("documentationPluginRegistry")
          PluginRegistry<DocumentationPlugin, DocumentationType>  documentationPlugins,
          @Qualifier("apiListingEnricherRegistry")
          PluginRegistry<ApiListingEnricher, DocumentationType> apiListingPlugins) {
    this.documentationPlugins = documentationPlugins;
    this.apiListingPlugins = apiListingPlugins;
  }


  public List<DocumentationPlugin> getDocumentationPluginsFor(DocumentationType type) {
    List<DocumentationPlugin> plugins = documentationPlugins.getPluginsFor(type);
    if (plugins.isEmpty()) {
      plugins.add(defaultDocumentationPlugin());
    }
    return plugins;
  }

  private DocumentationPlugin defaultDocumentationPlugin() {
    return new SwaggerSpringMvcPlugin();
  }

  public ApiListing enrich(ApiListingContext context) {
    for (ApiListingEnricher  each : apiListingPlugins.getPluginsFor(context.getDocumentationContext()
            .getDocumentationType())) {
      each.enrich(context);
    }
    return context.getApiListingBuilder().build();
  }
}
