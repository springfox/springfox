package com.mangofactory.swagger.mixins
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import com.mangofactory.swagger.models.*
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.alternates.AlternateTypeRule
import org.joda.time.LocalDate
import org.springframework.http.ResponseEntity

class ModelProviderSupport {
  ModelProvider defaultModelProvider() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def alternateTypeProvider = new AlternateTypeProvider()
    def accessors = new AccessorsProvider(resolver)
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(resolver,
            alternateTypeProvider, accessors, fields)
    modelPropertiesProvider.setObjectMapper(new ObjectMapper())
    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  ModelProvider providerThatSubstitutesLocalDateWithString() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def alternateTypeProvider = new AlternateTypeProvider()
    def accessors = new AccessorsProvider(resolver)
    alternateTypeProvider.addRule(new AlternateTypeRule(resolver.resolve(LocalDate), resolver.resolve(String)))
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(resolver, alternateTypeProvider,
            accessors, fields)
    modelPropertiesProvider.setObjectMapper(new ObjectMapper())

    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  ModelProvider providerThatSubstitutesResponseEntityOfVoid() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def alternateTypeProvider = new AlternateTypeProvider()
    def accessors = new AccessorsProvider(resolver)
    alternateTypeProvider.addRule(new AlternateTypeRule(resolver.resolve(ResponseEntity, Void),
            resolver.resolve(Void)))
    def modelPropertiesProvider = new DefaultModelPropertiesProvider(resolver, alternateTypeProvider,
            accessors, fields)
    modelPropertiesProvider.setObjectMapper(new ObjectMapper())

    def modelDependenciesProvider = modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
    new DefaultModelProvider(resolver, alternateTypeProvider, modelPropertiesProvider, modelDependenciesProvider)
  }

  private ModelDependencyProvider modelDependencyProvider(TypeResolver resolver,
    AlternateTypeProvider alternateTypeProvider, DefaultModelPropertiesProvider modelPropertiesProvider) {
    new ModelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
  }

  ModelDependencyProvider defaultModelDependencyProvider() {
    def resolver = new TypeResolver()
    def fields = new FieldsProvider(resolver)
    def alternateTypeProvider = new AlternateTypeProvider()
    def accessors = new AccessorsProvider(resolver)

    def modelPropertiesProvider = new DefaultModelPropertiesProvider(resolver, alternateTypeProvider, accessors, fields)
    modelPropertiesProvider.setObjectMapper(new ObjectMapper())
    modelDependencyProvider(resolver, alternateTypeProvider, modelPropertiesProvider)
  }

}
