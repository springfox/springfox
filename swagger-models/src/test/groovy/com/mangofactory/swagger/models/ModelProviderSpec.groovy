package com.mangofactory.swagger.models

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import org.springframework.http.HttpHeaders
import spock.lang.Specification

import static com.mangofactory.swagger.models.ResolvedTypes.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ModelProviderSpec extends Specification {

  def "dependencies provider respects ignorables"() {
    given:
      ModelProvider sut = defaultModelProvider()
      def context = ModelContext.inputParam(modelType)
      context.seen(asResolved(new TypeResolver(), HttpHeaders))
      def dependentTypeNames = sut.dependencies(context).keySet().sort()

    expect:
      dependencies == dependentTypeNames

    where:
      modelType                      | dependencies
      genericClassWithGenericField() | ["ResponseEntity«SimpleType»", "SimpleType"].sort()
  }

  def "dependencies are inferred correctly by the model provider"() {
    given:
      ModelProvider provider = defaultModelProvider()
      def dependentTypeNames = provider.dependencies(ModelContext.inputParam(modelType)).keySet().sort()

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
      genericClassWithGenericField() | ["Charset", "Entry«string,string»", "HttpHeaders", "MediaType", "ResponseEntity«SimpleType»", "SimpleType", "URI"].sort()
      genericClassWithDeepGenerics() | ["Charset", "Entry«string,string»", "HttpHeaders", "MediaType", "ResponseEntity«List«SimpleType»»", "SimpleType", "URI"].sort()
      genericCollectionWithEnum()    | ["Collection«string»"]
      recursiveType()                | ["SimpleType"]
  }
}
