package com.mangofactory.documentation.schema

import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.schema.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.documentation.spi.DocumentationType.*
import static com.mangofactory.documentation.spi.schema.contexts.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport, AlternateTypesSupport])
class ComplexTypeSpec extends Specification {
  def "complex type properties are inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType(), SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = provider.modelFor(returnValue(complexType(), SWAGGER_12, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "ComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

      asReturn.getName() == "ComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == typeName
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null

    where:
      property     | type         |  typeName     |qualifiedType
      "name"       | String       |  "string"     |"java.lang.String"
      "age"        | Integer.TYPE |  "int"        |"int"
      "category"   | Category     |  "Category"   |"com.mangofactory.documentation.schema.Category"
      "customType" | BigDecimal   |  "double"     |"java.math.BigDecimal"
  }

  def "recursive type properties are inferred correctly"() {
    given:
      def complexType = recursiveType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = provider.modelFor(returnValue(complexType, SWAGGER_12, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "RecursiveType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == "RecursiveType"
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

      asReturn.getName() == "RecursiveType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == "RecursiveType"
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null

    where:
      property | type           | qualifiedType
      "parent" | RecursiveType  | "com.mangofactory.documentation.schema.RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType, SWAGGER_12, alternateTypeProvider())).get()
      Model asReturn = provider.modelFor(returnValue(complexType, SWAGGER_12, alternateTypeProvider())).get()

    expect:
      asInput.getName() == "InheritedComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getModelRef().type == typeName
      !modelProperty.getModelRef().collection
      modelProperty.getModelRef().itemType == null

      asReturn.getName() == "InheritedComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getModelRef().type == typeName
      !retModelProperty.getModelRef().collection
      retModelProperty.getModelRef().itemType == null

    where:
      property            | type          | typeName   | typeProperty | qualifiedType
      "name"              | String        | "string"   | 'type'       | "java.lang.String"
      "age"               | Integer.TYPE  | "int"      | 'type'       | "int"
      "category"          | Category      | "Category" | 'reference'  | "com.mangofactory.documentation.schema.Category"
      "customType"        | BigDecimal    | "double"   | 'type'       | "java.math.BigDecimal"
      "inheritedProperty" | String        | "string"   | 'type'       | "java.lang.String"
  }
}
