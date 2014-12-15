package com.mangofactory.swagger.models

import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.mangofactory.swagger.models.dto.Model
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
      asInput.getName() == "ExampleWithEnums"
      asInput.getProperties().containsKey("exampleEnum")
      def modelPropertyOption = asInput.getProperties().get("exampleEnum")
      def modelProperty = modelPropertyOption


      def modelPropType = modelProperty.getType().getAbsoluteType()
      modelPropType == "string"
      modelProperty.getQualifiedType() == "com.mangofactory.swagger.models.ExampleEnum"
      modelProperty.getItems() == null
      Types.isBaseType(modelPropType)
      modelProperty.getAllowableValues().getValues() == list

      asReturn.getName() == "ExampleWithEnums"
      asReturn.getProperties().containsKey("exampleEnum")
      def retModelPropertyOption = asReturn.getProperties().get("exampleEnum")
      def retModelProperty = retModelPropertyOption
      def retPropType = retModelProperty.getType().dataType.type
      retPropType == "string"
      retModelProperty.getQualifiedType() == "com.mangofactory.swagger.models.ExampleEnum"
      retModelProperty.getItems() == null
      Types.isBaseType(retPropType)
      retModelProperty.getAllowableValues().getValues() == list
  }
}
