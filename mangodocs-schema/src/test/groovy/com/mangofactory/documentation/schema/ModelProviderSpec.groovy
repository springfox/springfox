package com.mangofactory.documentation.schema
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import org.springframework.http.HttpHeaders
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.documentation.spi.DocumentationType.*
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ModelProviderSpec extends Specification {

  def "dependencies provider respects ignorables"() {
    given:
      ModelProvider sut = defaultModelProvider()
      def context = inputParam(modelType, SWAGGER_12, alternateTypeProvider())
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
              alternateTypeProvider())).keySet().sort()

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
      genericClassWithGenericField() | ["Charset", "Entry«string,string»", "HttpHeaders", "MediaType", "ResponseEntityAlternative«SimpleType»", "SimpleType", "URI"].sort()
      genericClassWithDeepGenerics() | ["Charset", "Entry«string,string»", "HttpHeaders", "MediaType", "ResponseEntityAlternative«List«SimpleType»»", "SimpleType", "URI"].sort()
      genericCollectionWithEnum()    | ["Collection«string»"]
      recursiveType()                | ["SimpleType"]
  }
}
