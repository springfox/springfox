package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.Model
import spock.lang.Ignore
import spock.lang.Specification

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class SimpleTypeSpec extends Specification {
  def "simple types #property are rendered correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(simpleType())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(simpleType())).get()

    expect:
      asInput.name == "SimpleType"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      modelProperty.type == type
      modelProperty.format == format

      asReturn.name == "SimpleType"
      asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      retModelProperty.type == type
      retModelProperty.format == format

    where:
      property              | type      | format
      "aString"             | "string"  | null
      "date"                | "string"  | "date"
      "aByte"               | "string"  | null
      "aBoolean"            | "boolean" | null
      "aShort"              | "integer" | "int32"
      "anInt"               | "integer" | "int32"
      "aLong"               | "integer" | "int64"
      "aFloat"              | "number"  | "float"
      "aDouble"             | "number"  | "double"
//      "anObject"            | "object"  | null //TODO: Find out how to support this
      "anObjectByte"        | "string"  | null
      "anObjectBoolean"     | "boolean" | null
      "anObjectShort"       | "integer" | "int32"
      "anObjectInt"         | "integer" | "int32"
      "anObjectLong"        | "integer" | "int64"
      "anObjectFloat"       | "number"  | "float"
      "anObjectDouble"      | "number"  | "double"
  }

  @Ignore
  def "type with constructor all properties are infered"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(ModelContext.inputParam(typeWithConstructor())).get()
      Model asReturn = provider.modelFor(ModelContext.returnValue(typeWithConstructor())).get()

    expect:
      asInput.name == "TypeWithConstructor"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      modelProperty.type() == type
      modelProperty.qualifiedType() == qualifiedType
      modelProperty.items().isEmpty()

      asReturn.properties.name == "TypeWithConstructor"
      asReturn.properties."$property" == null

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
      asInput.name == "TypeWithJsonProperty"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      modelProperty.type == type

      asReturn.name == "TypeWithJsonProperty"
      asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      retModelProperty.type == type

    where:
      property              | type
      "some_odd_ball_name"  | "string"
  }
}
