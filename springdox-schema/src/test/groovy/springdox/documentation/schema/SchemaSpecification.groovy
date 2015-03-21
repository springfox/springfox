package springdox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import springdox.documentation.schema.mixins.ModelProviderSupport
import springdox.documentation.schema.mixins.SchemaPluginsSupport
import springdox.documentation.spi.DocumentationType

@Mixin([ModelProviderSupport, SchemaPluginsSupport])
class SchemaSpecification extends Specification {
  TypeNameExtractor typeNameExtractor
  ModelProvider modelProvider
  ModelDependencyProvider modelDependencyProvider
  DocumentationType documentationType = DocumentationType.SWAGGER_12
  def setup() {
    typeNameExtractor =
            new TypeNameExtractor(new TypeResolver(), defaultSchemaPlugins())
    modelProvider = defaultModelProvider()
    modelDependencyProvider = defaultModelDependencyProvider()
  }
}
