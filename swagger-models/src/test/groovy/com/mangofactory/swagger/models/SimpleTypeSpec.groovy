package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.model.Model
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
      asInput.name() == "SimpleType"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type)

      asReturn.name() == "SimpleType"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
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
  }

  @Ignore
  def "type with constructor all properties are infered"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(typeWithConstructor())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(typeWithConstructor())).get()

    expect:
      asInput.name() == "TypeWithConstructor"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type)

      asReturn.name() == "TypeWithConstructor"
      !asReturn.properties().contains(property)

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
      asInput.name() == "TypeWithJsonProperty"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type)

      asReturn.name() == "TypeWithJsonProperty"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
      Types.isBaseType(type)

    where:
      property              | type      | qualifiedType
      "some_odd_ball_name"  | "string"  | "java.lang.String"
  }
}
