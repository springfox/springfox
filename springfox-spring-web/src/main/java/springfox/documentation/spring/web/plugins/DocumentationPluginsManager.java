/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.plugins;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.SpringGroupingStrategy;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.*;

@Component
public class DocumentationPluginsManager {
  private final PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;
  private final PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins;
  private final PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins;
  private final PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugins;
  private final PluginRegistry<OperationBuilderPlugin, DocumentationType> operationBuilderPlugins;
  private final PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies;
  private final PluginRegistry<OperationModelsProviderPlugin, DocumentationType> operationModelsProviders;
  private final PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders;

  @Autowired
  public DocumentationPluginsManager(
          @Qualifier("documentationPluginRegistry")
          PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins,
          @Qualifier("apiListingBuilderPluginRegistry")
          PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins,
          @Qualifier("parameterBuilderPluginRegistry")
          PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins,
          @Qualifier("expandedParameterBuilderPluginRegistry")
          PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugins,
          @Qualifier("operationBuilderPluginRegistry")
          PluginRegistry<OperationBuilderPlugin, DocumentationType> operationBuilderPlugins,
          @Qualifier("resourceGroupingStrategyRegistry")
          PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies,
          @Qualifier("operationModelsProviderPluginRegistry")
          PluginRegistry<OperationModelsProviderPlugin, DocumentationType> operationModelsProviders,
          @Qualifier("defaultsProviderPluginRegistry")
          PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders) {
    this.documentationPlugins = documentationPlugins;
    this.apiListingPlugins = apiListingPlugins;
    this.parameterPlugins = parameterPlugins;
    this.parameterExpanderPlugins = parameterExpanderPlugins;
    this.operationBuilderPlugins = operationBuilderPlugins;
    this.resourceGroupingStrategies = resourceGroupingStrategies;
    this.operationModelsProviders = operationModelsProviders;
    this.defaultsProviders = defaultsProviders;
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
    for (ExpandedParameterBuilderPlugin each : parameterExpanderPlugins.getPluginsFor(context.getDocumentationType())) {
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
    return new Docket(DocumentationType.SWAGGER_12);
  }

  public DocumentationContextBuilder createContextBuilder(DocumentationType documentationType,
          DefaultConfiguration defaultConfiguration) {
    return defaultsProviders.getPluginFor(documentationType, defaultConfiguration)
            .create(documentationType)
            .withResourceGroupingStrategy(resourceGroupingStrategy(documentationType));
  }
}
