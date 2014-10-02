package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.Model
import scala.collection.JavaConversions
import spock.lang.Specification

import static com.google.common.collect.Lists.*
import static com.mangofactory.swagger.models.ModelContext.*

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class EnumTypeSpec extends Specification {
  def "enum type are inferred as type string with allowable values" () {
    given:
      def list = newArrayList("ONE", "TWO")
      def provider = defaultModelProvider()
      Model asInput = provider.modelFor(inputParam(enumType())).get()
      Model asReturn = provider.modelFor(returnValue(enumType())).get()

    expect:
      asInput.name() == "ExampleWithEnums"
      asInput.properties().contains("exampleEnum")
      def modelPropertyOption = asInput.properties().get("exampleEnum")
      def modelProperty = modelPropertyOption.get()
      modelProperty.type() == "string"
      modelProperty.qualifiedType() == "com.mangofactory.swagger.models.ExampleEnum"
      modelProperty.items().isEmpty()
      Types.isBaseType(modelProperty.type())
      modelProperty.allowableValues().values() == JavaConversions.collectionAsScalaIterable(list).toList()

      asReturn.name() == "ExampleWithEnums"
      asReturn.properties().contains("exampleEnum")
      def retModelPropertyOption = asReturn.properties().get("exampleEnum")
      def retModelProperty = retModelPropertyOption.get()
      retModelProperty.type() == "string"
      retModelProperty.qualifiedType() == "com.mangofactory.swagger.models.ExampleEnum"
      retModelProperty.items().isEmpty()
      Types.isBaseType(modelProperty.type())
      retModelProperty.allowableValues().values() == JavaConversions.collectionAsScalaIterable(list).toList()
  }
}
