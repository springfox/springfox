package com.mangofactory.schema

import com.mangofactory.swagger.mixins.TypesForTestingSupport

import static com.mangofactory.schema.plugins.ModelContext.*

@Mixin(TypesForTestingSupport)
class ModelDependencyProviderSpec extends SchemaSpecification {

  def "dependencies are inferred correctly" () {
    given:
      def context = inputParam(modelType, documentationType)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
          typeNameExtractor.typeName(inputParam(it, documentationType))
        }.unique()
        .sort()

    expect:
     dependencies == dependentTypeNames

    where:
      modelType                       | dependencies
      simpleType()                    | []
      complexType()                   | ["Category"]
      enumType()                      | []
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

  def "dependencies are inferred correctly for return parameters" () {
    given:
      def context = returnValue(modelType, documentationType)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
            typeNameExtractor.typeName(returnValue(it, documentationType))
          }.unique()
          .sort()
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
