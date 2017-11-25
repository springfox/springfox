/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

import com.google.common.base.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.PathDecorator;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.DefaultsProviderPlugin;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.OperationModelsProviderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.ResourceGroupingStrategy;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spi.service.contexts.PathContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.SpringGroupingStrategy;
import springfox.documentation.spring.web.scanners.ApiListingScanningContext;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static springfox.documentation.spring.web.plugins.DuplicateGroupsDetector.*;

@Component
public class DocumentationPluginsManager {
  @Autowired
  @Qualifier("documentationPluginRegistry")
  private PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins;
  @Autowired
  @Qualifier("apiListingBuilderPluginRegistry")
  private PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingPlugins;
  @Autowired
  @Qualifier("parameterBuilderPluginRegistry")
  private PluginRegistry<ParameterBuilderPlugin, DocumentationType> parameterPlugins;
  @Autowired
  @Qualifier("expandedParameterBuilderPluginRegistry")
  private PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugins;
  @Autowired
  @Qualifier("operationBuilderPluginRegistry")
  private PluginRegistry<OperationBuilderPlugin, DocumentationType> operationBuilderPlugins;
  @Autowired
  @Qualifier("resourceGroupingStrategyRegistry")
  private PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies;
  @Autowired
  @Qualifier("operationModelsProviderPluginRegistry")
  private PluginRegistry<OperationModelsProviderPlugin, DocumentationType> operationModelsProviders;
  @Autowired
  @Qualifier("defaultsProviderPluginRegistry")
  private PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders;
  @Autowired
  @Qualifier("pathDecoratorRegistry")
  private PluginRegistry<PathDecorator, DocumentationContext> pathDecorators;
  @Autowired
  @Qualifier("apiListingScannerPluginRegistry")
  private PluginRegistry<ApiListingScannerPlugin, DocumentationType> apiListingScanners;

  public Iterable<DocumentationPlugin> documentationPlugins() throws IllegalStateException {
    List<DocumentationPlugin> plugins = documentationPlugins.getPlugins();
    ensureNoDuplicateGroups(plugins);
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
    return new Docket(DocumentationType.SWAGGER_2);
  }

  public DocumentationContextBuilder createContextBuilder(
      DocumentationType documentationType,
      DefaultConfiguration defaultConfiguration) {
    return defaultsProviders.getPluginFor(documentationType, defaultConfiguration)
        .create(documentationType)
        .withResourceGroupingStrategy(resourceGroupingStrategy(documentationType));
  }

  public Function<String, String> decorator(final PathContext context) {
    return new Function<String, String>() {
      @Override
      public String apply(String input) {
        Iterable<Function<String, String>> decorators
            = from(pathDecorators.getPluginsFor(context.documentationContext()))
            .transform(toDecorator(context));
        String decorated = input;
        for (Function<String, String> decorator : decorators) {
          decorated = decorator.apply(decorated);
        }
        return decorated;
      }
    };
  }

  private Function<? super PathDecorator, Function<String, String>> toDecorator(final PathContext context) {
    return new Function<PathDecorator, Function<String, String>>() {
      @Override
      public Function<String, String> apply(PathDecorator input) {
        return input.decorator(context);
      }
    };
  }

  public Collection<ApiDescription> additionalListings(final ApiListingScanningContext context) {
    final DocumentationType documentationType = context.getDocumentationContext().getDocumentationType();
    List<ApiDescription> additional = newArrayList();
    for (ApiListingScannerPlugin each : apiListingScanners.getPluginsFor(documentationType)) {
      additional.addAll(each.apply(context.getDocumentationContext()));
    }
    return additional;
  }
}
