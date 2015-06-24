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

package springfox.documentation.swagger1.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.OperationModelsProvider
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import springfox.documentation.spring.web.readers.parameter.ParameterNameReader
import springfox.documentation.spring.web.scanners.MediaTypeReader
import springfox.documentation.swagger.readers.operation.SwaggerOperationModelsProvider
import springfox.documentation.swagger.readers.parameter.SwaggerExpandedParameterBuilder
import springfox.documentation.swagger.schema.ApiModelBuilder
import springfox.documentation.swagger.schema.ApiModelPropertyPropertyBuilder
import springfox.documentation.swagger.web.ClassOrApiAnnotationResourceGrouping

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class SwaggerPluginsSupport {
  SchemaPluginsManager swaggerSchemaPlugins() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
        OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyBuilder()))

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
        OrderAwarePluginRegistry.create(newArrayList(new ApiModelBuilder(new TypeResolver())))

    new SchemaPluginsManager(propRegistry, modelRegistry)
  }

  DocumentationPluginsManager swaggerServicePlugins(List<DefaultsProviderPlugin> swaggerDefaultsPlugins) {
    def resolver = new TypeResolver()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    plugins.documentationPlugins = OrderAwarePluginRegistry.create([])
    plugins.parameterExpanderPlugins =
        OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver), new SwaggerExpandedParameterBuilder()])
    plugins.parameterPlugins = OrderAwarePluginRegistry.create([new ParameterNameReader(),
        new springfox.documentation.swagger1.readers.parameter.ParameterNameReader()])
    plugins.operationBuilderPlugins = OrderAwarePluginRegistry.create([])
    plugins.resourceGroupingStrategies = OrderAwarePluginRegistry.create([new ClassOrApiAnnotationResourceGrouping()])
    plugins.operationModelsProviders = OrderAwarePluginRegistry.create([
        new OperationModelsProvider(resolver),
        new SwaggerOperationModelsProvider(resolver)])
    plugins.defaultsProviders = OrderAwarePluginRegistry.create(swaggerDefaultsPlugins)
    return plugins
  }
}
