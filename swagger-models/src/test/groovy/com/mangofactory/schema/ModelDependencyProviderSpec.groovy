package com.mangofactory.schema

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ModelDependencyProviderSpec extends Specification {
  def "dependencies are inferred correctly" () {
    given:
      ModelDependencyProvider provider = defaultModelDependencyProvider()
      def context = ModelContext.inputParam(modelType, documentationType())
      def dependentTypes = provider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() { ResolvedTypes.typeName(it) }.unique().sort()
    expect:
     dependencies == dependentTypeNames

    where:
      modelType                       | dependencies
      simpleType()                    | []
      complexType()                   | ["Category"]
      enumType()                      | []
      inheritedComplexType()          | ["Category"]
      typeWithLists()                 | ["List", "Category",  "ComplexType"].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType"]
      genericClass()                  | ["List", "SimpleType"]
      genericClassWithListField()     | ["List", "SimpleType"]
      genericClassWithGenericField()  | ["Charset", "HttpHeaders", "List",  "Map«string,string»", "MediaType",
                                         "ResponseEntityAlternative«SimpleType»", "Set", "SimpleType", "URI"].sort()
      genericClassWithDeepGenerics()  | ["Charset", "HttpHeaders", "List",  "Map«string,string»", "MediaType",
                                         "ResponseEntityAlternative«List«SimpleType»»", "Set", "SimpleType", "URI"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List"]
      recursiveType()                 | ["SimpleType"]
  }

  @Unroll
  def "dependencies are inferred correctly for return parameters" () {
    given:
      ModelDependencyProvider provider = defaultModelDependencyProvider()
      def context = ModelContext.returnValue(modelType, documentationType())
      def dependentTypes = provider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect { ResolvedTypes.responseTypeName(it) }.unique().sort()
    expect:
      dependencies == dependentTypeNames

    where:
      modelType                       | dependencies
      simpleType()                    | []
      complexType()                   | ["Category"]
      enumType()                      | []
      inheritedComplexType()          | ["Category"]
      typeWithLists()                 | ["List", "Category",  "ComplexType", "List[ComplexType]", "List[int]", "List[string]"].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType", "Set[ComplexType]", "Set[int]", "Set[string]"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType", "Array[ComplexType]", "Array[int]",
                                         "Array[string]", "Array[byte]"].sort()
      genericClass()                  | ["List[string]", "SimpleType"].sort()
      genericClassWithListField()     | ["List[string]", "List[SimpleType]", "SimpleType"].sort()
      genericClassWithGenericField()  | ["Charset", "HttpHeaders", "List[Charset]", "List[MediaType]", "List[string]",
                                         "Map«string,string»", "MediaType", "ResponseEntityAlternative«SimpleType»", "Set[string]",
                                         "SimpleType", "URI"].sort()
      genericClassWithDeepGenerics()  | ["Charset", "HttpHeaders", "List[Charset]", "List[MediaType]", "List[string]",
                                         "List[SimpleType]", "Map«string,string»", "MediaType",
                                         "ResponseEntityAlternative«List«SimpleType»»", "Set[string]", "SimpleType", "URI"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List[string]"].sort()
      recursiveType()                 | ["SimpleType"]
  }



}
