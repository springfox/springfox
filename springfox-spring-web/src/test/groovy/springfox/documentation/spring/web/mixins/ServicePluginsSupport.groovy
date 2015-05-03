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
import springfox.documentation.spi.service.*
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.OperationModelsProvider
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import springfox.documentation.spring.web.scanners.MediaTypeReader

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class ServicePluginsSupport {

  DocumentationPluginsManager defaultWebPlugins() {
    def resolver = new TypeResolver()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    plugins.documentationPlugins = OrderAwarePluginRegistry.create([])
    plugins.parameterExpanderPlugins = OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver)])
    plugins.parameterPlugins = OrderAwarePluginRegistry.create([])
    plugins.operationBuilderPlugins = OrderAwarePluginRegistry.create([])
    plugins.resourceGroupingStrategies = OrderAwarePluginRegistry.create([])
    plugins.operationModelsProviders = OrderAwarePluginRegistry.create([new OperationModelsProvider(resolver)])
    plugins.defaultsProviders = OrderAwarePluginRegistry.create([])
    return plugins
  }

  DocumentationPluginsManager customWebPlugins(List<DocumentationPlugin> documentationPlugins = [],
       List<ResourceGroupingStrategy> groupingStrategyPlugins = [],
       List<OperationBuilderPlugin> operationPlugins = [],
       List<ParameterBuilderPlugin> paramPlugins = [],
       List<DefaultsProviderPlugin> defaultProviderPlugins = []) {

    def resolver = new TypeResolver()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    plugins.documentationPlugins = OrderAwarePluginRegistry.create(documentationPlugins)
    plugins.parameterExpanderPlugins = OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver)])
    plugins.parameterPlugins = OrderAwarePluginRegistry.create(paramPlugins)
    plugins.operationBuilderPlugins = OrderAwarePluginRegistry.create(operationPlugins)
    plugins.resourceGroupingStrategies = OrderAwarePluginRegistry.create(groupingStrategyPlugins)
    plugins.operationModelsProviders = OrderAwarePluginRegistry.create([new OperationModelsProvider(resolver)])
    plugins.defaultsProviders = OrderAwarePluginRegistry.create(defaultProviderPlugins)
    return plugins
  }

}
