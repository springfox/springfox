package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.schema.plugins.ModelBuilderPlugin
import com.mangofactory.schema.plugins.ModelPropertyBuilderPlugin
import com.mangofactory.schema.plugins.SchemaPluginsManager
import com.mangofactory.springmvc.plugins.ApiListingBuilderPlugin
import com.mangofactory.springmvc.plugins.DocumentationPlugin
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin
import com.mangofactory.springmvc.plugins.ParameterExpanderPlugin
import com.mangofactory.swagger.plugins.ApiModelBuilderPlugin
import com.mangofactory.swagger.plugins.ApiModelPropertyPropertyBuilderPlugin
import com.mangofactory.swagger.plugins.operation.parameter.SwaggerParameterExpander
import com.mangofactory.swagger.readers.MediaTypeReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterExpander
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class PluginsSupport {

  SchemaPluginsManager pluginsManager() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyBuilderPlugin()))

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelBuilderPlugin(new TypeResolver())))

    new SchemaPluginsManager(propRegistry, modelRegistry)
  }

  DocumentationPluginsManager springPluginsManager() {
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(new TypeResolver())))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ParameterExpander(), new SwaggerParameterExpander()])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins)
  }
}
