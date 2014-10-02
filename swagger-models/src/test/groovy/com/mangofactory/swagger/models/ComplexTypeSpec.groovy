package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.Model
import spock.lang.Specification

import static com.mangofactory.swagger.models.ModelContext.inputParam
import static com.mangofactory.swagger.models.ModelContext.returnValue

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class ComplexTypeSpec extends Specification {
  def "complex type properties are inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType())).get()
      Model asReturn = provider.modelFor(returnValue(complexType())).get()

    expect:
      asInput.name == "ComplexType"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      modelProperty.type == type
      modelProperty.format == format
      if (modelProperty.metaClass.respondsTo(modelProperty, "get$ref")) {
        modelProperty.get$ref() == ref
      }

      asReturn.name == "ComplexType"
      asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      retModelProperty.type == type
      retModelProperty.format == format
      if (retModelProperty.metaClass.respondsTo(retModelProperty, "get$ref")) {
        retModelProperty.get$ref() == ref
      }

    where:
      property      | type          | format  | ref
      "name"        | "string"      | null    | null
      "age"         | "integer"     | "int32" | null
      "category"    | "ref"         | null    | "Category"
      "customType"  | "number"      | "double"| null
  }

  def "recursive type properties are inferred correctly"() {
    given:
    def complexType = recursiveType()
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam(complexType)).get()
    Model asReturn = provider.modelFor(returnValue(complexType)).get()

    expect:
      asInput.name == "RecursiveType"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      if (modelProperty.metaClass.respondsTo(modelProperty, "get$ref")) {
        modelProperty.get$ref() == ref
      }

      asReturn.name == "RecursiveType"
      asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      if (retModelProperty.metaClass.respondsTo(retModelProperty, "get$ref")) {
        retModelProperty.get$ref() == ref
      }

    where:
      property      | ref
      "parent"      | "RecursiveType"
  }

  def "inherited type properties are inferred correctly"() {
    given:
      def complexType = inheritedComplexType()
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(complexType)).get()
      Model asReturn = provider.modelFor(returnValue(complexType)).get()

    expect:
      asInput.name == "InheritedComplexType"
      asInput.properties."$property" != null
      def modelProperty = asInput.properties."$property"
      if (modelProperty.metaClass.respondsTo(modelProperty, "get$ref")) {
        modelProperty.get$ref() == ref
      }

      asReturn.name == "InheritedComplexType"
      asReturn.properties."$property" != null
      def retModelProperty = asReturn.properties."$property"
      if (retModelProperty.metaClass.respondsTo(retModelProperty, "get$ref")) {
        modelProperty.get$ref() == ref
      }

    where:
      property            | ref
      "name"              | "string"
      "age"               | "int"
      "category"          | "Category"
      "customType"        | "double"
      "inheritedProperty" | "string"
  }

}
