package com.mangofactory.schema
import com.mangofactory.schema.plugins.ModelContext
import com.mangofactory.service.model.Model
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Ignore
import spock.lang.Unroll

@Mixin(TypesForTestingSupport)
class SimpleTypeSpec extends SchemaSpecification {
  @Unroll
  def "simple type [#qualifiedType] is rendered as [#type]"() {
    given:
      Model asInput = modelProvider.modelFor(ModelContext.inputParam(simpleType(), documentationType())).get()
      Model asReturn = modelProvider.modelFor(ModelContext.returnValue(simpleType(), documentationType())).get()

    expect:
      asInput.getName() == "SimpleType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "SimpleType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

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
      Model asInput = modelProvider.modelFor(ModelContext.inputParam(typeWithConstructor(), documentationType)).get()
      Model asReturn = modelProvider.modelFor(ModelContext.returnValue(typeWithConstructor(), documentationType)).get()

    expect:
      asInput.getName() == "TypeWithConstructor"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType().erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "TypeWithConstructor"
      !asReturn.getProperties().containsKey(property)

    where:
      property      | type     | qualifiedType
      "stringValue" | String   | "java.lang.String"
  }

  def "Types with properties aliased using JsonProperty annotation"() {
    given:
      Model asInput = modelProvider.modelFor(ModelContext.inputParam(typeWithJsonPropertyAnnotation(), documentationType)).get()
      Model asReturn = modelProvider.modelFor(ModelContext.returnValue(typeWithJsonPropertyAnnotation(), documentationType)).get()

    expect:
      asInput.getName() == "TypeWithJsonProperty"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "TypeWithJsonProperty"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property             | type     | qualifiedType
      "some_odd_ball_name" | String   | "java.lang.String"
  }
}
