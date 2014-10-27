package com.mangofactory.swagger.readers.operation.parameter

import com.wordnik.swagger.models.parameters.*
import spock.lang.Specification
import spock.lang.Unroll

class SwaggerParameterBuilderSpec extends Specification {

  @Unroll
  def "should build a #expected parameter type"() {
    expect:
      SwaggerParameterBuilder builder = new SwaggerParameterBuilder()
              .withType(paramType)
              .withName('name')
              .withDescription('desc')
              .withRequired(false)

      def parameter = builder.build()
      parameter in expectedClass
      parameter.in == paramType
      action.call(parameter)

    where:
      paramType  | expectedClass   | action
      "query"    | QueryParameter  | { it.name == 'name' && it.description == 'desc' && it.required == false }
      "body"     | BodyParameter   | { it.name == 'name' && it.description == 'desc' && it.required == false }
      "path"     | PathParameter   | { it.name == 'name' && it.description == 'desc' && it.required == false }
      "cookie"   | CookieParameter | { it.name == 'name' && it.description == 'desc' && it.required == false }
      "header"   | HeaderParameter | { it.name == 'name' && it.description == 'desc' && it.required == false }
      "formData" | FormParameter   | { it.name == 'name' && it.description == 'desc' && it.required == false }
  }
}
