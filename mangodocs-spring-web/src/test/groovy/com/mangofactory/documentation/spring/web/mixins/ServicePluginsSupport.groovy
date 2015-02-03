package com.mangofactory.documentation.spring.web.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin
import com.mangofactory.documentation.spi.service.DocumentationPlugin
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin
import com.mangofactory.documentation.spi.service.OperationModelsProviderPlugin
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin
import com.mangofactory.documentation.spi.service.ExpandedParameterBuilderPlugin
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager
import com.mangofactory.documentation.spring.web.readers.operation.OperationModelsProvider
import com.mangofactory.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import com.mangofactory.documentation.spring.web.scanners.MediaTypeReader
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class ServicePluginsSupport {

  DocumentationPluginsManager defaultWebPlugins() {
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(new TypeResolver())))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ExpandedParameterBuilder()])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([new OperationModelsProvider(new TypeResolver())])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders)
  }

  DocumentationPluginsManager customWebPlugins(List<DocumentationPlugin> documentationPlugins = [],
      List<ResourceGroupingStrategy> groupingStrategyPlugins = [],
      List<OperationBuilderPlugin> operationPlugins = [],
      List<ParameterBuilderPlugin> paramPlugins = []) {
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(new TypeResolver())))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPluginRegistry =
            OrderAwarePluginRegistry.create(documentationPlugins)
    PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ExpandedParameterBuilder()])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create(paramPlugins)
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create(operationPlugins)
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create(groupingStrategyPlugins)
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([new OperationModelsProvider(new TypeResolver())])
    new DocumentationPluginsManager(documentationPluginRegistry, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders)
  }

}
