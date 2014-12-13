package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ModelDependencyProviderSpec extends Specification {

  @Unroll
  def "dependencies are inferred correctly" () {
    given:
      ModelDependencyProvider provider = defaultModelDependencyProvider()
      def context = ModelContext.inputParam(modelType)
      def dependentTypes = provider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() { ResolvedTypes.typeName(it) }.unique().sort()
    expect:
     dependencies == dependentTypeNames

    where:
    modelType                       | dependencies
    simpleType()                    | []
    complexType()                   | ["Category"]
    inheritedComplexType()          | ["Category"]
    typeWithLists()                 | ["List", "Category",  "ComplexType"].sort()
    typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
    typeWithArrays()                | ["Array", "Category", "ComplexType"]
    genericClass()                  | ["List", "SimpleType"]
    genericClassWithListField()     | ["List", "SimpleType"]
    genericClassWithGenericField()  | ["Charset", "HttpHeaders", "List",  "Map«string,string»", "MediaType", "ResponseEntityAlternative«SimpleType»", "Set", "SimpleType", "URI"].sort()
    genericClassWithDeepGenerics()  | ["Charset", "HttpHeaders", "List",  "Map«string,string»", "MediaType", "ResponseEntityAlternative«List«SimpleType»»", "Set", "SimpleType", "URI"].sort()
    genericCollectionWithEnum()     | ["Collection«string»", "List"]
    recursiveType()                 | ["SimpleType"]
  }



}
