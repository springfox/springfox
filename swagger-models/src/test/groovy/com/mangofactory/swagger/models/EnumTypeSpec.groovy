package com.mangofactory.swagger.models
import com.mangofactory.swagger.mixins.ModelProviderSupport
import com.mangofactory.swagger.mixins.TypesForTestingSupport
import com.wordnik.swagger.models.ModelImpl
import com.wordnik.swagger.models.properties.StringProperty
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList
import static com.mangofactory.swagger.models.ModelContext.inputParam
import static com.mangofactory.swagger.models.ModelContext.returnValue

@Mixin([TypesForTestingSupport, ModelProviderSupport])
class EnumTypeSpec extends Specification {
  def "enum type are inferred as type string with allowable values" () {
    given:
      def list = newArrayList("ONE", "TWO")
      def provider = defaultModelProvider()
      ModelImpl asInput = provider.modelFor(inputParam(enumType())).get()
      ModelImpl asReturn = provider.modelFor(returnValue(enumType())).get()

    expect:
      asInput.name == "ExampleWithEnums"
      assert asInput.properties.exampleEnum != null
      def modelProperty = asInput.properties.exampleEnum
      modelProperty.type == "string"
      ((StringProperty)modelProperty).getEnum() == list

      asReturn.name == "ExampleWithEnums"
      assert asReturn.properties.exampleEnum != null
      def retModelProperty = asReturn.properties.exampleEnum
      retModelProperty.type == "string"
      ((StringProperty)retModelProperty).getEnum() == list
  }
}
