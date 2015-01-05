package com.mangofactory.schema
import com.mangofactory.service.model.Model
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.schema.ModelContext.*

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
      modelProperty.typeName() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "ComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property     | type       |  qualifiedType
      "name"       | "string"   |  "java.lang.String"
      "age"        | "int"      |  "int"
      "category"   | "Category" |  "com.mangofactory.schema.Category"
      "customType" | "double"   |  "java.math.BigDecimal"
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
      modelProperty.typeName() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null
      Types.isBaseType(type) == isBaseType

      asReturn.getName() == "RecursiveType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null
      Types.isBaseType(type) == isBaseType

    where:
      property | type            | qualifiedType                           | isBaseType
      "parent" | "RecursiveType" | "com.mangofactory.schema.RecursiveType" | false
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
      modelProperty.typeName() == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "InheritedComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.typeName() == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property            | type       | typeProperty | qualifiedType
      "name"              | "string"   | 'type'       | "java.lang.String"
      "age"               | "int"      | 'type'       | "int"
      "category"          | "Category" | 'reference'  | "com.mangofactory.schema.Category"
      "customType"        | "double"   | 'type'       | "java.math.BigDecimal"
      "inheritedProperty" | "string"   | 'type'       | "java.lang.String"
  }
}
