package springdox.documentation.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry
import springdox.documentation.schema.DefaultTypeNameProvider
import springdox.documentation.schema.plugins.SchemaPluginsManager
import springdox.documentation.spi.DocumentationType
import springdox.documentation.spi.schema.ModelBuilderPlugin
import springdox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springdox.documentation.spi.schema.TypeNameProviderPlugin
import springdox.documentation.spi.service.ApiListingBuilderPlugin
import springdox.documentation.spi.service.DefaultsProviderPlugin
import springdox.documentation.spi.service.DocumentationPlugin
import springdox.documentation.spi.service.ExpandedParameterBuilderPlugin
import springdox.documentation.spi.service.OperationBuilderPlugin
import springdox.documentation.spi.service.OperationModelsProviderPlugin
import springdox.documentation.spi.service.ParameterBuilderPlugin
import springdox.documentation.spi.service.ResourceGroupingStrategy
import springdox.documentation.spring.web.plugins.DocumentationPluginsManager
import springdox.documentation.spring.web.readers.operation.OperationModelsProvider
import springdox.documentation.spring.web.readers.parameter.ExpandedParameterBuilder
import springdox.documentation.spring.web.scanners.MediaTypeReader
import springdox.documentation.swagger.readers.operation.SwaggerOperationModelsProvider
import springdox.documentation.swagger.readers.parameter.SwaggerExpandedParameterBuilder
import springdox.documentation.swagger.schema.ApiModelBuilder
import springdox.documentation.swagger.schema.ApiModelPropertyPropertyBuilder
import springdox.documentation.swagger.web.ClassOrApiAnnotationResourceGrouping

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
