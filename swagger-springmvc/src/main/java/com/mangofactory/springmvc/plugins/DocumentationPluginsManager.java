package com.mangofactory.springmvc.plugins;

import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.service.model.ApiListing;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DocumentationPluginsManager {

  private final PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;
  private final PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins;
  private final PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins;
  private final PluginRegistry<ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugins;

  @Autowired
  public DocumentationPluginsManager(
          @Qualifier("documentationPluginRegistry")
          PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins,
          @Qualifier("apiListingBuilderPluginRegistry")
          PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins,
          @Qualifier("parameterBuilderPluginRegistry")
          PluginRegistry <ParameterBuilderPlugin, DocumentationType> parameterPlugins,
          @Qualifier("parameterExpanderPluginRegistry")
          PluginRegistry <ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugins) {
    this.documentationPlugins = documentationPlugins;
    this.apiListingPlugins = apiListingPlugins;
    this.parameterPlugins = parameterPlugins;
    this.parameterExpanderPlugins = parameterExpanderPlugins;
  }


  public List<DocumentationPlugin> getDocumentationPluginsFor(DocumentationType type) {
    List<DocumentationPlugin> plugins = documentationPlugins.getPluginsFor(type);
    if (plugins.isEmpty()) {
      plugins.add(defaultDocumentationPlugin());
    }
    return plugins;
  }

  public Parameter parameter(ParameterContext parameterContext) {
    for (ParameterBuilderPlugin each : parameterPlugins.getPluginsFor(new DocumentationType("spring", "3+"))) {
      each.apply(parameterContext);
    }
    for (ParameterBuilderPlugin each : parameterPlugins.getPluginsFor(parameterContext.getDocumentationType())) {
      each.apply(parameterContext);
    }
    return parameterContext.parameterBuilder().build();
  }

  public Parameter expandParameter(ParameterExpansionContext context) {
    for (ParameterExpanderPlugin each : parameterExpanderPlugins.getPluginsFor(context.getDocumentationType())) {
      each.apply(context);
    }
    return context.getParameterBuilder().build();
  }

  private DocumentationPlugin defaultDocumentationPlugin() {
    return new SwaggerSpringMvcPlugin();
  }

  public ApiListing apiListing(ApiListingContext context) {
    for (ApiListingBuilderPlugin each : apiListingPlugins.getPluginsFor(context.getDocumentationContext()
            .getDocumentationType())) {
      each.apply(context);
    }
    return context.apiListingBuilder().build();
  }
}
