package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.Model
import spock.lang.Specification

import static com.google.common.base.Strings.*
import static com.mangofactory.swagger.models.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class GenericTypeSpec extends Specification{
  def "Generic property on a generic types is inferred correctly"() {
    given:
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(modelType)).get()
      Model asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
      asInput.properties.name == expectedModelName(modelNamePart)
      asInput.properties.genericField
      def modelProperty = asInput.properties.genericField
      modelProperty.get().type() == propertyType
      modelProperty.get().qualifiedType() == qualifiedType
      modelProperty.get().items().isEmpty() == !"List".equals(propertyType)

      asReturn.properties.name == expectedModelName(modelNamePart)
      asReturn.properties.genericField
      def retModelProperty = asReturn.properties.genericField
      retModelProperty.get().type() == propertyType
      retModelProperty.get().qualifiedType() ==qualifiedType
      retModelProperty.get().items().isEmpty() == !"List".equals(propertyType)

    where:
    modelType                       | propertyType                      | modelNamePart                     |  qualifiedType
    genericClass()                  | "SimpleType"                      | "SimpleType"                      | "com.mangofactory.swagger.models.SimpleType"
    genericClassWithTypeErased()    | "object"                          | ""                                | "java.lang.Object"
    genericClassWithListField()     | "List"                            | "List«SimpleType»"                | "java.util.List<com.mangofactory.swagger.models.SimpleType>"
    genericClassWithGenericField()  | "ResponseEntity«SimpleType»"      | "ResponseEntity«SimpleType»"      | "org.springframework.http.ResponseEntity<com.mangofactory.swagger.models.SimpleType>"
    genericClassWithDeepGenerics()  | "ResponseEntity«List«SimpleType»»"| "ResponseEntity«List«SimpleType»»"| "org.springframework.http.ResponseEntity<java.util.List<com.mangofactory.swagger.models.SimpleType>>"
    genericCollectionWithEnum()     | "Collection«string»"              | "Collection«string»"              | "java.util.Collection<com.mangofactory.swagger.models.ExampleEnum>"
  }


  def "Generic properties are inferred correctly even when they are not participating in the type bindings"() {
    given:
    def provider = defaultModelProvider()
    Model asInput = provider.modelFor(inputParam(modelType)).get()
    Model asReturn = provider.modelFor(returnValue(modelType)).get()

    expect:
    asInput.properties.strings
    def modelProperty = asInput.properties.strings
    modelProperty.get().type() == propertyType
//    modelProperty.get().qualifiedType() == qualifiedType DK TODO: Fix this

    asReturn.properties.("strings")
    def retModelProperty = asReturn.properties.strings
    retModelProperty.get().type() == propertyType
//    retModelProperty.get().qualifiedType() ==qualifiedType DK TODO: Fix this

    where:
    modelType                         | propertyType              |  qualifiedType
    genericClass()                    | "List"                    | "java.util.List<java.lang.String>"
    genericClassWithTypeErased()      | "List"                    | "java.util.List<java.lang.String>"
    genericClassWithListField()       | "List"                    | "java.util.List<java.lang.String>"
    genericClassWithGenericField()    | "List"                    | "java.util.List<java.lang.String>"
    genericClassWithDeepGenerics()    | "List"                    | "java.util.List<java.lang.String>"
    genericCollectionWithEnum()       | "List"                    | "java.util.List<java.lang.String>"
  }

  def expectedModelName(String modelName) {
    if (!isNullOrEmpty(modelName)) {
      String.format("GenericType«%s»", modelName)
    } else {
      "GenericType"
    }
  }
}
