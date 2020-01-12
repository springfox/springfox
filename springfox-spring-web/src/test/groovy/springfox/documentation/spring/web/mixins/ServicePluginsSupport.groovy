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

import groovy.lang.Mixin
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.mixins.SchemaPluginsSupport
import springfox.documentation.service.PathDecorator
import springfox.documentation.spi.service.ApiListingScannerPlugin
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spi.service.DocumentationPlugin
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.ParameterBuilderPlugin
import springfox.documentation.spi.service.ResourceGroupingStrategy
import springfox.documentation.spring.web.paths.OperationPathDecorator
import springfox.documentation.spring.web.paths.PathMappingDecorator
import springfox.documentation.spring.web.paths.PathSanitizer
import springfox.documentation.spring.web.paths.QueryStringUriTemplateDecorator
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
    plugins.apiListingPlugins = create(Stream.of(new MediaTypeReader(), new ApiListingReader()).collect(toList()))
    plugins.documentationPlugins = create([])
    plugins.parameterExpanderPlugins = create([new ExpandedParameterBuilder(resolver, enumTypeDeterminer)])
    plugins.parameterPlugins = create([new ParameterNameReader()])
    plugins.operationBuilderPlugins = create([])
    plugins.resourceGroupingStrategies = create([])
    plugins.operationModelsProviders = create([new OperationModelsProvider(defaultSchemaPlugins())])
    plugins.defaultsProviders = create([])
    plugins.apiListingScanners = create([])
    plugins.pathDecorators = create([
        new OperationPathDecorator(),
        new PathSanitizer(),
        new PathMappingDecorator(),
        new QueryStringUriTemplateDecorator()])
    return plugins
  }

  DocumentationPluginsManager customWebPlugins(
      List<DocumentationPlugin> documentationPlugins = [],
      List<ResourceGroupingStrategy> groupingStrategyPlugins = [],
      List<OperationBuilderPlugin> operationPlugins = [],
      List<ParameterBuilderPlugin> paramPlugins = [],
      List<DefaultsProviderPlugin> defaultProviderPlugins = [],
      List<PathDecorator> pathDecorators = [new OperationPathDecorator(),
                                            new PathSanitizer(),
                                            new PathMappingDecorator(),
                                            new QueryStringUriTemplateDecorator()],
      List<ApiListingScannerPlugin> listingScanners = []) {

    def resolver = new TypeResolver()
    def enumTypeDeterminer = new JacksonEnumTypeDeterminer()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = create(Stream.of(new MediaTypeReader(), new ApiListingReader()).collect(toList()))
    plugins.documentationPlugins = create(documentationPlugins)
    plugins.parameterExpanderPlugins = create([new ExpandedParameterBuilder(resolver, enumTypeDeterminer)])
    plugins.parameterPlugins = create(paramPlugins)
    plugins.operationBuilderPlugins = create(operationPlugins)
    plugins.resourceGroupingStrategies = create(groupingStrategyPlugins)
    plugins.operationModelsProviders = create([new OperationModelsProvider(defaultSchemaPlugins())])
    plugins.defaultsProviders = create(defaultProviderPlugins)
    plugins.pathDecorators = create(pathDecorators)
    plugins.apiListingScanners = create(listingScanners)
    return plugins
  }

}
