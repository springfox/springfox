package com.mangofactory.schema

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.plugins.DocumentationType
import com.mangofactory.swagger.mixins.ModelProviderSupport
import spock.lang.Specification

@Mixin(ModelProviderSupport)
class SchemaSpecification extends Specification {
  TypeNameExtractor typeNameExtractor
  ModelProvider modelProvider
  ModelDependencyProvider modelDependencyProvider
  DocumentationType documentationType = DocumentationType.SWAGGER_12
  def setup() {
    typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), pluginsManager())
    modelProvider = defaultModelProvider()
    modelDependencyProvider = defaultModelDependencyProvider()
  }
}
