package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
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
      asInput.getName() == "ComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type) == isBaseType

      asReturn.getName() == "ComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
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
      asInput.getName() == "RecursiveType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type) == isBaseType

      asReturn.getName() == "RecursiveType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
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
      asInput.getName() == "InheritedComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.getType() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type) == isBaseType

      asReturn.getName() == "InheritedComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.getType() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
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
