package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.schema.mixins.TypesForTestingSupport

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ModelProviderSpec extends Specification {

  def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "dependencies provider respects ignorables"() {
    given:
      ModelProvider sut = defaultModelProvider()
      def context = inputParam(modelType, SWAGGER_12, alternateTypeProvider(), namingStrategy)
      context.seen(new TypeResolver().resolve(HttpHeaders))
      def dependentTypeNames = sut.dependencies(context).keySet().sort()

    expect:
      dependencies == dependentTypeNames

    where:
      modelType                      | dependencies
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
  }

  @Unroll
  def "dependencies are inferred correctly by the model provider"() {
    given:
      ModelProvider provider = defaultModelProvider()
      def dependentTypeNames = provider.dependencies(inputParam(modelType, SWAGGER_12,
              alternateTypeProvider(), namingStrategy)).keySet().sort()

    expect:
      dependencies == dependentTypeNames

    where:
      modelType                      | dependencies
      simpleType()                   | []
      complexType()                  | ["Category"]
      inheritedComplexType()         | ["Category"]
      typeWithLists()                | ["Category", "ComplexType"].sort()
      typeWithSets()                 | ["Category", "ComplexType"].sort()
      typeWithArrays()               | ["Category", "ComplexType"]
      genericClass()                 | ["SimpleType"]
      genericClassWithListField()    | ["SimpleType"]
      genericClassWithGenericField() | ["ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics() | ["ResponseEntityAlternative«List«SimpleType»»", "SimpleType"].sort()
      genericCollectionWithEnum()    | ["Collection«string»"]
      recursiveType()                | ["SimpleType"]
  }
}
