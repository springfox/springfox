package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.DefaultTypeNameProvider
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.schema.plugins.ModelBuilderPlugin
import com.mangofactory.schema.plugins.TypeNameProviderPlugin
import com.mangofactory.schema.plugins.ModelPropertyBuilderPlugin
import com.mangofactory.schema.plugins.SchemaPluginsManager
import com.mangofactory.spring.web.ResourceGroupingStrategy
import com.mangofactory.spring.web.plugins.ApiListingBuilderPlugin
import com.mangofactory.spring.web.plugins.Defaults
import com.mangofactory.spring.web.plugins.DocumentationPlugin
import com.mangofactory.spring.web.plugins.DocumentationPluginsManager
import com.mangofactory.spring.web.plugins.OperationBuilderPlugin
import com.mangofactory.spring.web.plugins.OperationModelsProviderPlugin
import com.mangofactory.spring.web.plugins.ParameterBuilderPlugin
import com.mangofactory.spring.web.plugins.ParameterExpanderPlugin
import com.mangofactory.spring.web.readers.MediaTypeReader
import com.mangofactory.spring.web.readers.OperationModelsProvider
import com.mangofactory.spring.web.readers.operation.parameter.ParameterExpander
import com.mangofactory.swagger.plugins.ApiModelBuilderPlugin
import com.mangofactory.swagger.plugins.ApiModelPropertyPropertyBuilderPlugin
import com.mangofactory.swagger.plugins.operation.SwaggerOperationModelsProvider
import com.mangofactory.swagger.plugins.operation.parameter.SwaggerParameterExpander
import com.mangofactory.swagger.web.ClassOrApiAnnotationResourceGrouping
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin(ModelProviderSupport)
class PluginsSupport {

  SchemaPluginsManager pluginsManager() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyBuilderPlugin()))

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelBuilderPlugin(new TypeResolver())))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new DefaultTypeNameProvider()))

    new SchemaPluginsManager(propRegistry, modelRegistry, modelNameRegistry)
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
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create([new ClassOrApiAnnotationResourceGrouping()])
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([
              new OperationModelsProvider(new TypeResolver(), defaultAlternateTypesProvider()),
              new SwaggerOperationModelsProvider(new TypeResolver(), defaultAlternateTypesProvider())])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders)
  }

  DocumentationPluginsManager springPluginsManagerWithDefaults(Defaults defaults) {
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(defaults.typeResolver)))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ParameterExpanderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ParameterExpander(), new SwaggerParameterExpander()])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create([new ClassOrApiAnnotationResourceGrouping()])
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([
                    new OperationModelsProvider(defaults.typeResolver, defaults.alternateTypeProvider),
                    new SwaggerOperationModelsProvider(defaults.typeResolver, defaults.alternateTypeProvider)])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders)
  }
}
