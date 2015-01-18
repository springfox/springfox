package com.mangofactory.swagger.plugins

import com.mangofactory.schema.ExampleWithEnums
import com.mangofactory.schema.TypeWithApiModelAnnotation
import com.mangofactory.schema.TypeWithEmptyApiModelAnnotation
import spock.lang.Specification

class ApiModelTypeNameProviderSpec extends Specification {
  def "renders the type names correctly" () {
    given:
      def sut = new ApiModelTypeNameProvider()
    when:
      def name = sut.nameFor(clazz)
    then:
      name == expectedName
    where:
      clazz                           | expectedName
      ExampleWithEnums                | "ExampleWithEnums"
      TypeWithApiModelAnnotation      | "ApiModelTest"
      TypeWithEmptyApiModelAnnotation | "TypeWithEmptyApiModelAnnotation"
  }
}
