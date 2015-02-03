package com.mangofactory.documentation.builders
import com.mangofactory.documentation.service.model.AllowableListValues
import spock.lang.Specification

class ParameterBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod     | value                                 | property
      'name'            | 'param1'                              | 'name'
      'description'     | 'param1 desc'                         | 'description'
      'defaultValue'    | 'default'                             | 'defaultValue'
      'dataType'        | 'string'                              | 'parameterType'
      'parameterType'   | 'string'                              | 'paramType'
      'parameterAccess' | 'public'                              | 'paramAccess'
      'allowMultiple'   | true                                  | 'allowMultiple'
      'required'        | true                                  | 'required'
      'allowableValues' | new AllowableListValues([], "LIST")   | 'allowableValues'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod     | value                                 | property
      'name'            | 'param1'                              | 'name'
      'description'     | 'param1 desc'                         | 'description'
      'defaultValue'    | 'default'                             | 'defaultValue'
      'dataType'        | 'string'                              | 'parameterType'
      'parameterType'   | 'string'                              | 'paramType'
      'parameterAccess' | 'public'                              | 'paramAccess'
      'allowableValues' | new AllowableListValues([], "LIST")   | 'allowableValues'
  }
}
