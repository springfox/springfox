package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
import spock.lang.Ignore
import spock.lang.Specification

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class SimpleTypeSpec extends Specification {
  def "simple types are rendered correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(simpleType())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(simpleType())).get()

    expect:
      asInput.getName() == "SimpleType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "SimpleType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
      Types.isBaseType(type)

    where:
      property              | type      | qualifiedType
      "aString"             | "string"  | "java.lang.String"
      "aByte"               | "byte"    | "byte"
      "aBoolean"            | "boolean" | "boolean"
      "aShort"              | "int"     | "int"
      "anInt"               | "int"     | "int"
      "aLong"               | "long"    | "long"
      "aFloat"              | "float"   | "float"
      "aDouble"             | "double"  | "double"
      "anObject"            | "object"  | "java.lang.Object"
      "anObjectByte"        | "byte"    | "java.lang.Byte"
      "anObjectBoolean"     | "boolean" | "java.lang.Boolean"
      "anObjectShort"       | "int"     | "java.lang.Short"
      "anObjectInt"         | "int"     | "java.lang.Integer"
      "anObjectLong"        | "long"    | "java.lang.Long"
      "anObjectFloat"       | "float"   | "java.lang.Float"
      "anObjectDouble"      | "double"  | "java.lang.Double"
      "currency"            | "string"  | "java.util.Currency"
  }

  @Ignore
  def "type with constructor all properties are infered"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(typeWithConstructor())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(typeWithConstructor())).get()

    expect:
      asInput.getName() == "TypeWithConstructor"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "TypeWithConstructor"
      !asReturn.getProperties().containsKey(property)

    where:
      property              | type      | qualifiedType
      "stringValue"         | "string"  | "java.lang.String"
  }

  def "Types with properties aliased using JsonProperty annotation"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(typeWithJsonPropertyAnnotation())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(typeWithJsonPropertyAnnotation())).get()

    expect:
      asInput.getName() == "TypeWithJsonProperty"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "TypeWithJsonProperty"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
      Types.isBaseType(type)

    where:
      property              | type      | qualifiedType
      "some_odd_ball_name"  | "string"  | "java.lang.String"
  }
}
