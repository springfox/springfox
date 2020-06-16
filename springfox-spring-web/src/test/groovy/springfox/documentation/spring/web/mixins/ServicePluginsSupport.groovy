/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.PathDecorator
import springfox.documentation.spi.service.ApiListingScannerPlugin
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spi.service.DocumentationPlugin
import springfox.documentation.spi.service.ModelNamesRegistryFactoryPlugin
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.ParameterBuilderPlugin

import springfox.documentation.spring.web.paths.OperationPathDecorator
import springfox.documentation.spring.web.paths.PathMappingDecorator
import springfox.documentation.spring.web.paths.PathSanitizer
import springfox.documentation.spring.web.paths.QueryStringUriTemplateDecorator
import springfox.documentation.spring.web.plugins.DefaultResponseTypeReader
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager
import springfox.documentation.spring.web.readers.operation.OperationModelsProvider
import springfox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import springfox.documentation.spring.web.readers.parameter.ParameterNameReader
import springfox.documentation.spring.web.scanners.ApiListingReader
import springfox.documentation.spring.web.scanners.MediaTypeReader

import java.util.stream.Stream

import static java.util.stream.Collectors.*
import static org.springframework.plugin.core.OrderAwarePluginRegistry.*

@SuppressWarnings("GrMethodMayBeStatic")
trait ServicePluginsSupport implements SchemaPluginsSupport {

  DocumentationPluginsManager defaultWebPlugins() {
    def resolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = of(Stream.of(new MediaTypeReader(), new ApiListingReader()).collect(toList()))
    plugins.documentationPlugins = of([])
    plugins.parameterExpanderPlugins = of([new ExpandedParameterBuilder(
        resolver,
        enumTypeDeterminer
    )])
    plugins.parameterPlugins = of([new ParameterNameReader()])
    plugins.operationBuilderPlugins = of([])
    plugins.operationModelsProviders = of([new OperationModelsProvider(defaultSchemaPlugins())])
    plugins.defaultsProviders = of([])
    plugins.apiListingScanners = of([])
    plugins.pathDecorators = of([
        new OperationPathDecorator(),
        new PathSanitizer(),
        new PathMappingDecorator(),
        new QueryStringUriTemplateDecorator()])
    plugins.responsePlugins = of([new DefaultResponseTypeReader()])
    plugins.modelNameRegistryFactoryPlugins = of([])
    return plugins
  }

  DocumentationPluginsManager customWebPlugins(
      List<DocumentationPlugin> documentationPlugins = [],
      List<OperationBuilderPlugin> operationPlugins = [],
      List<ParameterBuilderPlugin> paramPlugins = [],
      List<DefaultsProviderPlugin> defaultProviderPlugins = [],
      List<PathDecorator> pathDecorators = [new OperationPathDecorator(),
                                            new PathSanitizer(),
                                            new PathMappingDecorator(),
                                            new QueryStringUriTemplateDecorator()],
      List<ApiListingScannerPlugin> listingScanners = [],
      ModelNamesRegistryFactoryPlugin[] modelNamesGenerators) {

    def resolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = of(Stream.of(new MediaTypeReader(), new ApiListingReader()).collect(toList()))
    plugins.documentationPlugins = of(documentationPlugins)
    plugins.parameterExpanderPlugins = of([new ExpandedParameterBuilder(
        resolver,
        enumTypeDeterminer
    )])
    plugins.parameterPlugins = of(paramPlugins)
    plugins.operationBuilderPlugins = of(operationPlugins)
    plugins.operationModelsProviders = of([new OperationModelsProvider(defaultSchemaPlugins())])
    plugins.defaultsProviders = of(defaultProviderPlugins)
    plugins.pathDecorators = of(pathDecorators)
    plugins.apiListingScanners = of(listingScanners)
    plugins.modelNameRegistryFactoryPlugins = of(modelNamesGenerators)
    return plugins
  }

}
