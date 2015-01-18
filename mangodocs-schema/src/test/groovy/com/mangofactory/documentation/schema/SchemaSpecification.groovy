package com.mangofactory.documentation.schema

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.SchemaPluginsSupport
import com.mangofactory.documentation.spi.DocumentationType
import spock.lang.Specification

@Mixin([ModelProviderSupport, SchemaPluginsSupport])
class SchemaSpecification extends Specification {
  TypeNameExtractor typeNameExtractor
  ModelProvider modelProvider
  ModelDependencyProvider modelDependencyProvider
  DocumentationType documentationType = DocumentationType.SWAGGER_12
  def setup() {
    typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(), defaultSchemaPlugins())
    modelProvider = defaultModelProvider()
    modelDependencyProvider = defaultModelDependencyProvider()
  }
}
