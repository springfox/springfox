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

package springfox.documentation.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.mock.env.MockEnvironment
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.JacksonJsonViewProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.ViewProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext
import springfox.documentation.spi.service.DefaultsProviderPlugin
import springfox.documentation.spring.web.DescriptionResolver
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
import springfox.documentation.swagger.web.SwaggerApiListingReader

import java.util.stream.Stream

import static java.util.Collections.*
import static java.util.stream.Collectors.*
import static org.springframework.plugin.core.OrderAwarePluginRegistry.*

@SuppressWarnings("GrMethodMayBeStatic")
trait SwaggerPluginsSupport {
  SchemaPluginsManager swaggerSchemaPlugins() {
    def resolver = new TypeResolver()
    def descriptions = new DescriptionResolver(new MockEnvironment())
    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        of(singletonList(new DefaultTypeNameProvider()))
    def typeNameExtractor = new TypeNameExtractor(
        resolver,
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
        of(singletonList(new ApiModelPropertyPropertyBuilder(descriptions)))

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
        of([new ApiModelBuilder(resolver, typeNameExtractor, new JacksonEnumTypeDeterminer())])

    PluginRegistry<ViewProviderPlugin, DocumentationType> viewProviderRegistry =
        of([new JacksonJsonViewProvider(new TypeResolver())])

    PluginRegistry<SyntheticModelProviderPlugin, ModelContext> syntheticModelRegistry =
        of(new ArrayList<>())

    new SchemaPluginsManager(propRegistry, modelRegistry, viewProviderRegistry, syntheticModelRegistry)
  }

  DocumentationPluginsManager swaggerServicePlugins(List<DefaultsProviderPlugin> swaggerDefaultsPlugins) {
    def resolver = new TypeResolver()
    def plugins = new DocumentationPluginsManager()
    plugins.apiListingPlugins = of(Stream.of(new MediaTypeReader(), new SwaggerApiListingReader()).collect(toList()))
    plugins.documentationPlugins = of([])
    def descriptions = new DescriptionResolver(new MockEnvironment())
    plugins.parameterExpanderPlugins =
        create([
            new ExpandedParameterBuilder(resolver, new JacksonEnumTypeDeterminer()),
            new SwaggerExpandedParameterBuilder(descriptions, new JacksonEnumTypeDeterminer())])
    plugins.parameterPlugins = of([new ParameterNameReader(),
                                       new ParameterNameReader()])
    plugins.operationBuilderPlugins = of([])
    plugins.resourceGroupingStrategies = of([new ClassOrApiAnnotationResourceGrouping()])
    plugins.apiListingScanners = of([])
    plugins.operationModelsProviders = of([
        new OperationModelsProvider(swaggerSchemaPlugins()),
        new SwaggerOperationModelsProvider(resolver)])
    plugins.defaultsProviders = of(swaggerDefaultsPlugins)
    plugins.apiListingScanners = of([])
    return plugins
  }
}
