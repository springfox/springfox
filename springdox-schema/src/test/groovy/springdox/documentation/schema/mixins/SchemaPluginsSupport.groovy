package springdox.documentation.schema.mixins

import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springdox.documentation.schema.DefaultTypeNameProvider
import springdox.documentation.schema.plugins.SchemaPluginsManager
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.schema.ModelBuilderPlugin
import springdox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springdox.documentation.spi.schema.TypeNameProviderPlugin

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
