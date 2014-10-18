package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.ModelImpl
import com.wordnik.swagger.models.properties.ArrayProperty
import com.wordnik.swagger.models.properties.Property
import com.wordnik.swagger.models.properties.RefProperty
import spock.lang.Specification

import static com.google.common.base.Strings.isNullOrEmpty
import static com.mangofactory.swagger.models.ModelContext.inputParam
import static com.mangofactory.swagger.models.ModelContext.returnValue

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class GenericTypeSpec extends Specification{
  def "Generic property on a generic types is inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(inputParam(modelType)).get()
      ModelImpl asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
      asInput.name == expectedModelName(typeName)
      assert asInput.properties.genericField != null
      Property modelProperty = asInput.properties.genericField
      assert modelProperty.class == propertyType
      if (propertyType == RefProperty) {
        assert ((RefProperty)modelProperty).$ref == typeName
      } else if (propertyType == ArrayProperty) {
        def element = ((ArrayProperty)modelProperty).items
        if (element.class == RefProperty) {
          assert ((RefProperty)element).$ref == "SimpleType"
        } else if (element.class == ArrayProperty) {
          assert ((ArrayProperty)element).items.type == typeName
        } else {
          assert ((ObjectProperty)element).type == "object"
        }
      } else {
        assert ((ObjectProperty)modelProperty).type == "object"
      }

      asReturn.name == expectedModelName(typeName)
      asReturn.properties.genericField
      def retModelProperty = asReturn.properties.genericField
      assert retModelProperty.class == propertyType
      if (propertyType == RefProperty) {
        assert ((RefProperty)retModelProperty).$ref == typeName
      } else if (propertyType == ArrayProperty) {
        def element = ((ArrayProperty)retModelProperty).items
        if (element.class == RefProperty) {
          assert ((RefProperty)element).$ref == "SimpleType"
        } else if (element.class == ArrayProperty) {
          assert ((ArrayProperty)element).items.type == typeName
        } else {
          assert ((ObjectProperty)element).type == "object"
        }
      } else {
        assert ((ObjectProperty)retModelProperty).type == "object"
      }

    where:
    modelType                       | propertyType    | typeName
    genericClass()                  | RefProperty     | "SimpleType"
    genericClassWithTypeErased()    | ObjectProperty  | ""
    genericClassWithListField()     | ArrayProperty   | "List«SimpleType»"
    genericClassWithGenericField()  | RefProperty     | "ResponseEntity«SimpleType»"
    genericClassWithDeepGenerics()  | RefProperty     | "ResponseEntity«List«SimpleType»»"
    genericCollectionWithEnum()     | RefProperty     | "Collection«string»"
  }


  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam(modelType)).get()
    Model asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
    asInput.properties.strings
    def modelProperty = asInput.properties.strings
    assert modelProperty.type == "array"

    asReturn.properties.("strings")
    def retModelProperty = asReturn.properties.strings
    assert retModelProperty.type == "array"

    where:
    modelType << [genericClass(),
                  genericClassWithTypeErased(),
                  genericClassWithListField(),
                  genericClassWithGenericField(),
                  genericClassWithDeepGenerics(),
                  genericCollectionWithEnum()]
  }

  def expectedModelName(String modelName) {
    if (!isNullOrEmpty(modelName)) {
      String.format("GenericType«%s»", modelName)
    } else {
      "GenericType"
    }
  }
}
