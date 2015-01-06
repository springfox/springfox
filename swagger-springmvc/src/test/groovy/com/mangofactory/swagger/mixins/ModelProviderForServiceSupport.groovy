package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.mangofactory.documentation.plugins.DocumentationType
import com.mangofactory.documentation.plugins.ModelEnricher
import com.mangofactory.documentation.plugins.ModelPropertyEnricher
import com.mangofactory.documentation.plugins.PluginsManager
import com.mangofactory.schema.DefaultModelProvider
import com.mangofactory.schema.ModelDependencyProvider
import com.mangofactory.schema.ModelProvider
import com.mangofactory.schema.ObjectMapperBeanPropertyNamingStrategy
import com.mangofactory.schema.alternates.AlternateTypeProvider
import com.mangofactory.schema.property.bean.AccessorsProvider
import com.mangofactory.schema.property.bean.BeanModelPropertyProvider
import com.mangofactory.schema.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.schema.property.field.FieldModelPropertyProvider
import com.mangofactory.schema.property.field.FieldProvider
import com.mangofactory.schema.property.provider.DefaultModelPropertiesProvider
import com.mangofactory.springmvc.plugins.ApiListingEnricher
import com.mangofactory.springmvc.plugins.DocumentationPlugin
import com.mangofactory.springmvc.plugins.DocumentationPluginsManager
import com.mangofactory.swagger.plugins.ApiModelEnricher
import com.mangofactory.swagger.plugins.ApiModelPropertyPropertyEnricher
import com.mangofactory.swagger.readers.MediaTypeReader
import org.springframework.plugin.core.OrderAwarePluginRegistry
import org.springframework.plugin.core.PluginRegistry

import static com.google.common.collect.Lists.*

@SuppressWarnings("GrMethodMayBeStatic")
class ModelProviderForServiceSupport {


  def pluginsManager() {
    PluginRegistry<ModelPropertyEnricher, DocumentationType> propRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelPropertyPropertyEnricher()))

    PluginRegistry<ModelEnricher, DocumentationType> modelRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new ApiModelEnricher(new TypeResolver())))

    new PluginsManager(propRegistry, modelRegistry)
  }

  def springPluginsManager() {
    PluginRegistry<ApiListingEnricher, DocumentationType> apiListingRegistry =
            OrderAwarePluginRegistry.create(newArrayList(new MediaTypeReader(new TypeResolver())))
    PluginRegistry<DocumentationPlugin, DocumentationType> documentationPlugins =
            OrderAwarePluginRegistry.create([])
    new DocumentationPluginsManager(documentationPlugins, apiListingRegistry)
  }

  ModelProvider modelProvider(TypeResolver typeResolver = new TypeResolver(),
                              AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()) {

    def fields = new FieldProvider(typeResolver)
    def pluginsManager = pluginsManager()
    def objectMapper = new ObjectMapper()
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy, pluginsManager)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy, pluginsManager)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy, pluginsManager)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider, pluginsManager)
  }

  ModelProvider modelProviderWithSnakeCaseNamingStrategy(TypeResolver typeResolver = new TypeResolver(),
                              AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()) {

    def fields = new FieldProvider(typeResolver)
    def pluginsManager = pluginsManager()
    def objectMapper = new ObjectMapper()
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES)
    def namingStrategy = new ObjectMapperBeanPropertyNamingStrategy(objectMapper)

    def beanModelPropertyProvider = new BeanModelPropertyProvider(new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider, namingStrategy, pluginsManager)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(fields, alternateTypeProvider, namingStrategy, pluginsManager)
    def constructorModelPropertyProvider =
            new ConstructorModelPropertyProvider(fields, alternateTypeProvider, namingStrategy, pluginsManager)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)

    modelPropertiesProvider.objectMapper = objectMapper
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider,
            pluginsManager)
  }


}
