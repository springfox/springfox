package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.model.Model
import spock.lang.Specification

import static com.mangofactory.swagger.models.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ComplexTypeSpec extends Specification {
  def "complex type properties are inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType())).get()
      Model asReturn = provider.modelFor(returnValue(complexType())).get()

    expect:
      asInput.name() == "ComplexType"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

      asReturn.name() == "ComplexType"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

    where:
      property      | type          | qualifiedType                               | isBaseType
      "name"        | "string"      | "java.lang.String"                          | true
      "age"         | "int"         | "int"                                       | true
      "category"    | "Category"    | "com.mangofactory.swagger.models.Category"  | false
      "customType"  | "double"      | "java.math.BigDecimal"                      | true
  }

  def "recursive type properties are inferred correctly"() {
    given:
    def complexType = recursiveType()
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam(complexType)).get()
    Model asReturn = provider.modelFor(returnValue(complexType)).get()

    expect:
      asInput.name() == "RecursiveType"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

      asReturn.name() == "RecursiveType"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

    where:
      property      | type            | qualifiedType                                   | isBaseType
      "parent"      | "RecursiveType" | "com.mangofactory.swagger.models.RecursiveType" | false
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType)).get()
      Model asReturn = provider.modelFor(returnValue(complexType)).get()

    expect:
      asInput.name() == "InheritedComplexType"
      asInput.properties().contains(property)
      def modelProperty = asInput.properties().get(property)
      modelProperty.get().type() == type
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

      asReturn.name() == "InheritedComplexType"
      asReturn.properties().contains(property)
      def retModelProperty = asReturn.properties().get(property)
      retModelProperty.get().type() == type
      retModelProperty.get().qualifiedType() == qualifiedType
      retModelProperty.get().items().isEmpty()
      Types.isBaseType(type) == isBaseType

    where:
      property            | type          | qualifiedType                               | isBaseType
      "name"              | "string"      | "java.lang.String"                          | true
      "age"               | "int"         | "int"                                       | true
      "category"          | "Category"    | "com.mangofactory.swagger.models.Category"  | false
      "customType"        | "double"      | "java.math.BigDecimal"                      | true
      "inheritedProperty" | "string"      | "java.lang.String"                          | true
  }
}
