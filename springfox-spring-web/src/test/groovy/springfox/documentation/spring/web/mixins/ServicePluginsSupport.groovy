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

package springfox.documentation.spring.web.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.ApiListingBuilderPlugin
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spi.service.DocumentationPlugin
import springfox.documentation.spi.service.ExpandedParameterBuilderPlugin
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.OperationModelsProviderPlugin
import springfox.documentation.spi.service.ParameterBuilderPlugin
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.OperationModelsProvider
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import springfox.documentation.spring.web.scanners.MediaTypeReader

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class ServicePluginsSupport {

  DocumentationPluginsManager defaultWebPlugins() {
    def resolver = new TypeResolver()
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver)])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([new OperationModelsProvider(resolver)])
    PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders =
            OrderAwarePluginRegistry.create([])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders, defaultsProviders)
  }

  DocumentationPluginsManager customWebPlugins(List<DocumentationPlugin> documentationPlugins = [],
      List<ResourceGroupingStrategy> groupingStrategyPlugins = [],
      List<OperationBuilderPlugin> operationPlugins = [],
      List<ParameterBuilderPlugin> paramPlugins = [],
      List<DefaultsProviderPlugin> defaultProviderPlugins = []) {
    def resolver = new TypeResolver()
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPluginRegistry =
            OrderAwarePluginRegistry.create(documentationPlugins)
    PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver)])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create(paramPlugins)
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create(operationPlugins)
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create(groupingStrategyPlugins)
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([new OperationModelsProvider(resolver)])
    PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders =
            OrderAwarePluginRegistry.create(defaultProviderPlugins)
    new DocumentationPluginsManager(documentationPluginRegistry, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders, defaultsProviders)
  }

}
