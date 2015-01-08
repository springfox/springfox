package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.plugins.DocumentationType
import com.mangofactory.documentation.plugins.ModelEnricher
import com.mangofactory.documentation.plugins.ModelPropertyEnricher
import com.mangofactory.documentation.plugins.PluginsManager
import com.mangofactory.springmvc.plugins.ApiListingBuilderPlugin
import com.mangofactory.springmvc.plugins.DocumentationPlugin
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager
import com.mangofactory.springmvc.plugins.OperationBuilderPlugin
import com.mangofactory.springmvc.plugins.ParameterBuilderPlugin
import com.mangofactory.springmvc.plugins.ParameterExpanderPlugin
import com.mangofactory.swagger.plugins.ApiModelEnricher
import com.mangofactory.swagger.plugins.ApiModelPropertyPropertyEnricher
import com.mangofactory.swagger.plugins.operation.parameter.SwaggerParameterExpander
import com.mangofactory.swagger.readers.MediaTypeReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterExpander
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class PluginsSupport {

  PluginsManager pluginsManager() {
    PluginRegistry<ModelPropertyEnricher, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyEnricher()))

    PluginRegistry<ModelEnricher, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelEnricher(new TypeResolver())))

    new PluginsManager(propRegistry, modelRegistry)
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
