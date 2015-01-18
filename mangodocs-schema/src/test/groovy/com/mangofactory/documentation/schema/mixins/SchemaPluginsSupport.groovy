package com.mangofactory.documentation.schema.mixins
import com.mangofactory.documentation.schema.DefaultTypeNameProvider
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.schema.ModelBuilderPlugin
import com.mangofactory.documentation.spi.schema.ModelPropertyBuilderPlugin
import com.mangofactory.documentation.spi.schema.TypeNameProviderPlugin
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

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
