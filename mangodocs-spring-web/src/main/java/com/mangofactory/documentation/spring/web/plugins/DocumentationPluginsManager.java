package com.mangofactory.documentation.spring.web.plugins;

import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Operation;
import com.mangofactory.documentation.service.model.Parameter;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.contexts.ModelContext;
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ApiListingContext;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.OperationContext;
import com.mangofactory.documentation.spi.service.OperationModelsProviderPlugin;
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterContext;
import com.mangofactory.documentation.spi.service.ParameterExpanderPlugin;
import com.mangofactory.documentation.spi.service.contexts.ParameterExpansionContext;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import com.mangofactory.documentation.spring.web.SpringGroupingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

@Component
public class DocumentationPluginsManager {

  private final PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;
  private final PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins;
  private final PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins;
  private final PluginRegistry<ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugins;
  private final PluginRegistry<OperationBuilderPlugin, DocumentationType> operationBuilderPlugins;
  private final PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies;
  private final PluginRegistry<OperationModelsProviderPlugin, DocumentationType> operationModelsProviders;

  @Autowired
  public DocumentationPluginsManager(
          @Qualifier("documentationPluginRegistry")
          PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins,
          @Qualifier("apiListingBuilderPluginRegistry")
          PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins,
          @Qualifier("parameterBuilderPluginRegistry")
          PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins,
          @Qualifier("parameterExpanderPluginRegistry")
          PluginRegistry<ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugins,
          @Qualifier("operationBuilderPluginRegistry")
          PluginRegistry<OperationBuilderPlugin, DocumentationType> operationBuilderPlugins,
          @Qualifier("resourceGroupingStrategyRegistry")
          PluginRegistry <ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies,
          @Qualifier("operationModelsProviderPluginRegistry")
          PluginRegistry <OperationModelsProviderPlugin, DocumentationType> operationModelsProviders) {
    this.documentationPlugins = documentationPlugins;
    this.apiListingPlugins = apiListingPlugins;
    this.parameterPlugins = parameterPlugins;
    this.parameterExpanderPlugins = parameterExpanderPlugins;
    this.operationBuilderPlugins = operationBuilderPlugins;
    this.resourceGroupingStrategies = resourceGroupingStrategies;
    this.operationModelsProviders = operationModelsProviders;
  }


  public List<DocumentationPlugin> documentationPlugins() {
    List<DocumentationPlugin> plugins = documentationPlugins.getPlugins();
    if (plugins.isEmpty()) {
      return newArrayList(defaultDocumentationPlugin());
    }
    return plugins;
  }

  public Parameter parameter(ParameterContext parameterContext) {
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

  public Operation operation(OperationContext operationContext) {
    for (OperationBuilderPlugin each : operationBuilderPlugins.getPluginsFor(operationContext.getDocumentationType())) {
      each.apply(operationContext);
    }
    return operationContext.operationBuilder().build();
  }

  public ApiListing apiListing(ApiListingContext context) {
    for (ApiListingBuilderPlugin each : apiListingPlugins.getPluginsFor(context.getDocumentationType())) {
      each.apply(context);
    }
    return context.apiListingBuilder().build();
  }

  public Set<ModelContext> modelContexts(RequestMappingContext context) {
    DocumentationType documentationType = context.getDocumentationContext().getDocumentationType();
    for (OperationModelsProviderPlugin each : operationModelsProviders.getPluginsFor(documentationType)) {
      each.apply(context);
    }
    return context.operationModelsBuilder().build();
  }

  public ResourceGroupingStrategy resourceGroupingStrategy(DocumentationType documentationType) {
    return resourceGroupingStrategies.getPluginFor(documentationType, new SpringGroupingStrategy());
  }

  private DocumentationPlugin defaultDocumentationPlugin() {
    return new DocumentationConfigurer(DocumentationType.SWAGGER_12);
  }
}
