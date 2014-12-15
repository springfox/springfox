package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class SimpleTypeSpec extends Specification {
  @Unroll
  def "simple type [#qualifiedType] is rendered as [#type]"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(simpleType())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(simpleType())).get()

    expect:
      asInput.getName() == "SimpleType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType().getAbsoluteType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "SimpleType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().getAbsoluteType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property          | type      | qualifiedType
      "aString"         | "string"  | "java.lang.String"
      "aByte"           | "string"  | "byte"
      "aBoolean"        | "boolean" | "boolean"
      "aShort"          | "integer" | "int"
      "anInt"           | "integer" | "int"
      "aLong"           | "integer" | "long"
      "aFloat"          | "integer" | "float"
      "aDouble"         | "number"  | "double"
      "anObjectByte"    | "string"  | "java.lang.Byte"
      "anObjectBoolean" | "boolean" | "java.lang.Boolean"
      "anObjectShort"   | "integer" | "java.lang.Short"
      "anObjectInt"     | "integer" | "java.lang.Integer"
      "anObjectLong"    | "integer" | "java.lang.Long"
      "anObjectFloat"   | "integer" | "java.lang.Float"
      "anObjectDouble"  | "number"  | "java.lang.Double"
      "currency"        | "string"  | "java.util.Currency"
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
      property      | type     | qualifiedType
      "stringValue" | "string" | "java.lang.String"
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
      modelProperty.getType().getAbsoluteType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type)

      asReturn.getName() == "TypeWithJsonProperty"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType().getAbsoluteType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
      Types.isBaseType(type)

    where:
      property             | type     | qualifiedType
      "some_odd_ball_name" | "string" | "java.lang.String"
  }
}
