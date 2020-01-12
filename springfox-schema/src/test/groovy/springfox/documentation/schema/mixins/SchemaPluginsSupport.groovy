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
package springfox.documentation.schema.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.schema.JacksonEnumTypeDeterminer
import springfox.documentation.schema.JacksonJsonViewProvider
import springfox.documentation.schema.TypeNameExtractor
import springfox.documentation.schema.plugins.PropertyDiscriminatorBasedInheritancePlugin
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.SyntheticModelProviderPlugin
import springfox.documentation.spi.schema.TypeNameProviderPlugin
import springfox.documentation.spi.schema.ViewProviderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext

trait SchemaPluginsSupport {
  @SuppressWarnings("GrMethodMayBeStatic")
  SchemaPluginsManager defaultSchemaPlugins() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
        OrderAwarePluginRegistry.create(new ArrayList<>())

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
        OrderAwarePluginRegistry.create([new DefaultTypeNameProvider()])
    TypeNameExtractor typeNameExtractor = new TypeNameExtractor(
        new TypeResolver(),
        modelNameRegistry,
        new JacksonEnumTypeDeterminer())

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
        OrderAwarePluginRegistry.create(
            [new PropertyDiscriminatorBasedInheritancePlugin(
                new TypeResolver(), new JacksonEnumTypeDeterminer(), typeNameExtractor)])

    PluginRegistry<ViewProviderPlugin, DocumentationType> viewProviderRegistry =
        OrderAwarePluginRegistry.create([new JacksonJsonViewProvider(new TypeResolver())])

    PluginRegistry<SyntheticModelProviderPlugin, ModelContext> syntheticModelRegistry =
        OrderAwarePluginRegistry.create(new ArrayList<>())

    new SchemaPluginsManager(propRegistry, modelRegistry, viewProviderRegistry, syntheticModelRegistry)
  }
}
