package springfox.documentation.schema.mixins

import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springfox.documentation.schema.plugins.SchemaPluginsManager
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.schema.DefaultTypeNameProvider
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.TypeNameProviderPlugin

import static com.google.common.collect.Lists.*

class SchemaPluginsSupport {
  @SuppressWarnings("GrMethodMayBeStatic")
  SchemaPluginsManager defaultSchemaPlugins() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList())

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList())

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new DefaultTypeNameProvider()))

    new SchemaPluginsManager(propRegistry, modelRegistry, modelNameRegistry)
  }
}
