package com.mangofactory.documentation.schema

import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import spock.lang.Unroll

import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ModelDependencyProviderSpec extends SchemaSpecification {

  @Unroll
  def "dependencies are inferred correctly" () {
    given:
      def context = inputParam(modelType, documentationType, alternateTypeProvider())
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
          typeNameExtractor.typeName(inputParam(it, documentationType, alternateTypeProvider()))
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
                                         "MediaType", "ResponseEntityAlternative«SimpleType»", "Set[string]",
                                         "SimpleType", "URI", "List[Entry«string,string»]", "Entry«string,string»",
                                         "Map«string,string»"].sort()
      genericClassWithDeepGenerics()  | ["Charset", "HttpHeaders", "List[Charset]", "List[MediaType]", "List[string]",
                                         "List[SimpleType]", "List[Entry«string,string»]", "MediaType",
                                         "Entry«string,string»", "ResponseEntityAlternative«List«SimpleType»»",
                                         "Set[string]", "SimpleType", "URI", "Map«string,string»"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List[string]"].sort()
      recursiveType()                 | ["SimpleType"]
  }

  @Unroll
  def "dependencies are inferred correctly for return parameters" () {
    given:
      def context = returnValue(modelType, documentationType, alternateTypeProvider())
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
            typeNameExtractor.typeName(returnValue(it, documentationType, alternateTypeProvider()))
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
                                         "MediaType", "ResponseEntityAlternative«SimpleType»",
                                         "Set[string]", "SimpleType", "URI"].sort()
      genericClassWithDeepGenerics()  | ["Charset", "HttpHeaders", "List[Charset]", "List[MediaType]", "List[string]",
                                         "List[SimpleType]", "MediaType", "ResponseEntityAlternative«List«SimpleType»»",
                                         "Set[string]", "SimpleType", "URI"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List[string]"].sort()
      recursiveType()                 | ["SimpleType"]
  }



}
