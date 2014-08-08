package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.DefaultModelProvider
import com.mangofactory.swagger.models.ModelDependencyProvider
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.property.bean.AccessorsProvider
import com.mangofactory.swagger.models.property.bean.BeanModelPropertyProvider
import com.mangofactory.swagger.models.property.constructor.ConstructorModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldModelPropertyProvider
import com.mangofactory.swagger.models.property.field.FieldProvider
import com.mangofactory.swagger.models.property.provider.DefaultModelPropertiesProvider

class ModelProviderSupport {

  ModelProvider modelProvider(TypeResolver typeResolver = new TypeResolver(),
                              AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider()) {

    def fields = new FieldProvider(typeResolver)

    def objectMapper = new ObjectMapper()

    def beanModelPropertyProvider = new BeanModelPropertyProvider(objectMapper, new AccessorsProvider(typeResolver), typeResolver,
            alternateTypeProvider)
    def fieldModelPropertyProvider = new FieldModelPropertyProvider(objectMapper, fields, alternateTypeProvider)
    def constructorModelPropertyProvider = new ConstructorModelPropertyProvider(objectMapper, fields,
            alternateTypeProvider)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(beanModelPropertyProvider,
            fieldModelPropertyProvider, constructorModelPropertyProvider)
    def modelDependenciesProvider = new ModelDependencyProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(typeResolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }


}
