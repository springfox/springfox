package com.mangofactory.schema
import com.mangofactory.service.model.Model
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import spock.lang.Specification

import static com.mangofactory.schema.plugins.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ComplexTypeSpec extends Specification {

  def "complex type properties are inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType(), documentationType())).get()
      Model asReturn = provider.modelFor(returnValue(complexType(), documentationType())).get()

    expect:
      asInput.getName() == "ComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "ComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property     | type         |  qualifiedType
      "name"       | String       |  "java.lang.String"
      "age"        | Integer.TYPE |  "int"
      "category"   | Category     |  "com.mangofactory.schema.Category"
      "customType" | BigDecimal   |  "java.math.BigDecimal"
  }

  def "recursive type properties are inferred correctly"() {
    given:
      def complexType = recursiveType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType, documentationType())).get()
      Model asReturn = provider.modelFor(returnValue(complexType, documentationType())).get()

    expect:
      asInput.getName() == "RecursiveType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "RecursiveType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property | type           | qualifiedType
      "parent" | RecursiveType  | "com.mangofactory.schema.RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType, documentationType())).get()
      Model asReturn = provider.modelFor(returnValue(complexType, documentationType())).get()

    expect:
      asInput.getName() == "InheritedComplexType"
      asInput.getProperties().containsKey(property)
      def modelProperty = asInput.getProperties().get(property)
      modelProperty.type.erasedType == type
      modelProperty.getQualifiedType() == qualifiedType
      modelProperty.getItems() == null

      asReturn.getName() == "InheritedComplexType"
      asReturn.getProperties().containsKey(property)
      def retModelProperty = asReturn.getProperties().get(property)
      retModelProperty.type.erasedType == type
      retModelProperty.getQualifiedType() == qualifiedType
      retModelProperty.getItems() == null

    where:
      property            | type          | typeProperty | qualifiedType
      "name"              | String        | 'type'       | "java.lang.String"
      "age"               | Integer.TYPE  | 'type'       | "int"
      "category"          | Category      | 'reference'  | "com.mangofactory.schema.Category"
      "customType"        | BigDecimal    | 'type'       | "java.math.BigDecimal"
      "inheritedProperty" | String        | 'type'       | "java.lang.String"
  }
}
