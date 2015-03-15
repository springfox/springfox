package com.mangofactory.documentation.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.DefaultTypeNameProvider
import com.mangofactory.documentation.schema.plugins.SchemaPluginsManager
import com.mangofactory.documentation.spi.DocumentationType
import com.mangofactory.documentation.spi.schema.ModelBuilderPlugin
import com.mangofactory.documentation.spi.schema.ModelPropertyBuilderPlugin
import com.mangofactory.documentation.spi.schema.TypeNameProviderPlugin
import com.mangofactory.documentation.spi.service.ApiListingBuilderPlugin
import com.mangofactory.documentation.spi.service.DefaultsProviderPlugin
import com.mangofactory.documentation.spi.service.DocumentationPlugin
import com.mangofactory.documentation.spi.service.ExpandedParameterBuilderPlugin
import com.mangofactory.documentation.spi.service.OperationBuilderPlugin
import com.mangofactory.documentation.spi.service.OperationModelsProviderPlugin
import com.mangofactory.documentation.spi.service.ParameterBuilderPlugin
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy
import com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsManager
import com.mangofactory.documentation.spring.web.readers.operation.OperationModelsProvider
import com.mangofactory.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import com.mangofactory.documentation.spring.web.scanners.MediaTypeReader
import com.mangofactory.documentation.swagger.readers.operation.SwaggerOperationModelsProvider
import com.mangofactory.documentation.swagger.readers.parameter.SwaggerExpandedParameterBuilder
import com.mangofactory.documentation.swagger.schema.ApiModelBuilder
import com.mangofactory.documentation.swagger.schema.ApiModelPropertyPropertyBuilder
import com.mangofactory.documentation.swagger.web.ClassOrApiAnnotationResourceGrouping
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class SwaggerPluginsSupport {
  SchemaPluginsManager swaggerSchemaPlugins() {
    PluginRegistry<ModelPropertyBuilderPlugin, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyBuilder()))

    PluginRegistry<ModelBuilderPlugin, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelBuilder(new TypeResolver())))

    PluginRegistry<TypeNameProviderPlugin, DocumentationType> modelNameRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new DefaultTypeNameProvider()))

    new SchemaPluginsManager(propRegistry, modelRegistry, modelNameRegistry)
  }

  DocumentationPluginsManager swaggerServicePlugins(List<DefaultsProviderPlugin> swaggerDefaultsPlugins) {
    def resolver = new TypeResolver()
    PluginRegistry<ApiListingBuilderPlugin, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(resolver)))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ExpandedParameterBuilderPlugin, DocumentationType> parameterExpanderPlugin =
            OrderAwarePluginRegistry.create([new ExpandedParameterBuilder(resolver), new SwaggerExpandedParameterBuilder()])
    PluginRegistry<ParameterBuilderPlugin, DocumentationType>  parameterBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<OperationBuilderPlugin, DocumentationType>  operationBuilderPlugins=
            OrderAwarePluginRegistry.create([])
    PluginRegistry<ResourceGroupingStrategy, DocumentationType> resourceGroupingStrategies =
            OrderAwarePluginRegistry.create([new ClassOrApiAnnotationResourceGrouping()])
    PluginRegistry<OperationModelsProviderPlugin, DocumentationType> modelProviders =
            OrderAwarePluginRegistry.create([
              new OperationModelsProvider(resolver),
              new SwaggerOperationModelsProvider(resolver)])
    PluginRegistry<DefaultsProviderPlugin, DocumentationType> defaultsProviders =
            OrderAwarePluginRegistry.create(swaggerDefaultsPlugins)
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry, parameterBuilderPlugins,
            parameterExpanderPlugin, operationBuilderPlugins, resourceGroupingStrategies, modelProviders, defaultsProviders)
  }
}
