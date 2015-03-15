package springdox.documentation.schema

import spock.lang.Ignore
import spock.lang.Unroll
import springdox.documentation.schema.mixins.TypesForTestingSupport

import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, AlternateTypesSupport])
class SimpleTypeSpec extends SchemaSpecification {
  @Unroll
  def "simple type [#qualifiedType] is rendered as [#type]"() {
    given:
      Model asInput = modelProvider.modelFor(inputParam(simpleType(), SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(simpleType(), SWAGGER_12, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "SimpleType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.modelRef
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturn.getName() == "SimpleType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      def retItem = retModelProperty.modelRef
      retItem.type == Types.typeNameFor(type)
      !retItem.collection
      retItem.itemType == null

    where:
      property          | type    | qualifiedType
      "aString"         | String  | "java.lang.String"
      "aByte"           | byte    | "byte"
      "aBoolean"        | boolean | "boolean"
      "aShort"          | short   | "int"
      "anInt"           | int     | "int"
      "aLong"           | long    | "long"
      "aFloat"          | float   | "float"
      "aDouble"         | double  | "double"
      "anObjectByte"    | Byte    | "java.lang.Byte"
      "anObjectBoolean" | Boolean | "java.lang.Boolean"
      "anObjectShort"   | Short   | "java.lang.Short"
      "anObjectInt"     | Integer | "java.lang.Integer"
      "anObjectLong"    | Long    | "java.lang.Long"
      "anObjectFloat"   | Float   | "java.lang.Float"
      "anObjectDouble"  | Double  | "java.lang.Double"
      "currency"        | Currency| "java.util.Currency"
  }

  @Ignore
  def "type with constructor all properties are inferred"() {
    given:
      Model asInput = modelProvider.modelFor(inputParam(typeWithConstructor(), documentationType, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(typeWithConstructor(), documentationType, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "TypeWithConstructor"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType().erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.getModelRef()
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturn.getName() == "TypeWithConstructor"
      !asReturn.getProperties().containsKey(property)

    where:
      property      | type     | qualifiedType
      "stringValue" | String   | "java.lang.String"
  }

  def "Types with properties aliased using JsonProperty annotation"() {
    given:
      Model asInput = modelProvider.modelFor(inputParam(typeWithJsonPropertyAnnotation(), documentationType, alternateTypeProvider())).get()
      Model asReturn = modelProvider.modelFor(returnValue(typeWithJsonPropertyAnnotation(), documentationType, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "TypeWithJsonProperty"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      def item = modelProperty.getModelRef()
      item.type == Types.typeNameFor(type)
      !item.collection
      item.itemType == null

      asReturn.getName() == "TypeWithJsonProperty"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      def retItem = retModelProperty.getModelRef()
      retItem.type == Types.typeNameFor(type)
      !retItem.collection
      retItem.itemType == null

    where:
      property             | type     | qualifiedType
      "some_odd_ball_name" | String   | "java.lang.String"
  }


}
