package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.schema.plugins.ModelContext;
import com.mangofactory.service.model.ApiListing;
import com.mangofactory.service.model.Operation;
import com.mangofactory.service.model.Parameter;
import com.mangofactory.spring.web.ResourceGroupingStrategy;
import com.mangofactory.spring.web.SpringGroupingStrategy;
import com.mangofactory.spring.web.scanners.RequestMappingContext;
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

  public Operation operation(OperationContext operationContext) {
    for (OperationBuilderPlugin each : operationBuilderPlugins.getPluginsFor(operationContext.getDocumentationType())) {
      each.apply(operationContext);
    }
    return operationContext.operationBuilder().build();
  }

  public ApiListing apiListing(ApiListingContext context) {
    for (ApiListingBuilderPlugin each : apiListingPlugins.getPluginsFor(context.getDocumentationContext()
            .getDocumentationType())) {
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
