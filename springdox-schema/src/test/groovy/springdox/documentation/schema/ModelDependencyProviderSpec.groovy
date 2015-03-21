package springdox.documentation.schema

import spock.lang.Unroll
import springdox.documentation.schema.mixins.TypesForTestingSupport

import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class ModelDependencyProviderSpec extends SchemaSpecification {
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  @Unroll
  def "dependencies are inferred correctly" () {
    given:
      def context = inputParam(modelType, documentationType, alternateTypeProvider(), namingStrategy)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
          typeNameExtractor.typeName(inputParam(it, documentationType, alternateTypeProvider(), namingStrategy))
        }.unique()
        .sort()

    expect:
     dependencies == dependentTypeNames

    where:
      modelType                       | dependencies
      simpleType()                    | []
      complexType()                   | ["Category"]
      enumType()                      | []
      typeWithLists()                 | ["List", "Category",  "ComplexType"].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType"].sort()
      genericClass()                  | ["List", "SimpleType"].sort()
      genericClassWithListField()     | ["List", "SimpleType"].sort()
      genericClassWithGenericField()  | ["HttpHeaders", "List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics()  | ["HttpHeaders", "List", "ResponseEntityAlternative«List«SimpleType»»",
                                         "SimpleType"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List"].sort()
      recursiveType()                 | ["SimpleType"]
  }

  @Unroll
  def "dependencies are inferred correctly for return parameters" () {
    given:
      def context = returnValue(modelType, documentationType, alternateTypeProvider(), namingStrategy)
      def dependentTypes = modelDependencyProvider.dependentModels(context)
      def dependentTypeNames = dependentTypes.collect() {
            typeNameExtractor.typeName(returnValue(it, documentationType, alternateTypeProvider(), namingStrategy))
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
      typeWithLists()                 | ["List", "Category",  "ComplexType" ].sort()
      typeWithSets()                  | ["Set", "Category",  "ComplexType"].sort()
      typeWithArrays()                | ["Array", "Category", "ComplexType"].sort()
      genericClass()                  | ["List", "SimpleType"].sort()
      genericClassWithListField()     | ["List", "SimpleType"].sort()
      genericClassWithGenericField()  | ["HttpHeaders", "List", "ResponseEntityAlternative«SimpleType»", "SimpleType"].sort()
      genericClassWithDeepGenerics()  | ["HttpHeaders", "List", "ResponseEntityAlternative«List«SimpleType»»",
                                         "SimpleType"].sort()
      genericCollectionWithEnum()     | ["Collection«string»", "List"].sort()
      recursiveType()                 | ["SimpleType"]
  }



}
