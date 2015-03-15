package springdox.documentation.schema

import springdox.documentation.schema.mixins.TypesForTestingSupport

import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class TypeNameExtractorSpec extends SchemaSpecification {
  def "Response class for container types are inferred correctly"() {
    given:
      def context = returnValue(containerType, SWAGGER_12, alternateTypeProvider())
    expect:
      typeNameExtractor.typeName(context) == name

    where:
      containerType                  | name
      genericListOfSimpleType()      | "List"
      genericListOfInteger()         | "List"
      erasedList()                   | "List"
      genericSetOfSimpleType()       | "Set"
      genericSetOfInteger()          | "Set"
      erasedSet()                    | "Set"
      genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"
      hashMap(String, SimpleType)    | "Map«string,SimpleType»"
      hashMap(String, String)        | "Map«string,string»"
  }
  
  def "Input class for container types are inferred correctly"() {
    given:
      def context = returnValue(containerType, SWAGGER_12, alternateTypeProvider())
    expect:
      typeNameExtractor.typeName(context) == name

    where:
      containerType                  | name
      genericListOfSimpleType()      | "List"
      genericListOfInteger()         | "List"
      erasedList()                   | "List"
      genericSetOfSimpleType()       | "Set"
      genericSetOfInteger()          | "Set"
      erasedSet()                    | "Set"
      genericClassWithGenericField() | "GenericType«ResponseEntityAlternative«SimpleType»»"
      hashMap(String, SimpleType)    | "Map«string,SimpleType»"
      hashMap(String, String)        | "Map«string,string»"
  }
  //TODO: test cases for parent (withAndWithout)
}
