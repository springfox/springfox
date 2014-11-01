package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import com.mangofactory.swagger.models.ModelProvider
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.wordnik.swagger.models.Model
import com.wordnik.swagger.models.parameters.*
import org.springframework.core.MethodParameter
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.models.ResolvedTypes.asResolved

class SwaggerParameterBuilderSpec extends Specification {

  @Unroll
  def "should build a #expected parameter type"() {
    setup:
      ModelProvider modelProvider = Mock {
        modelFor(_) >> Optional.of(Mock(Model))
      }
      MethodParameter methodParameter = Stub(MethodParameter)
      ResolvedMethodParameter resolvedMethodParameter = new ResolvedMethodParameter(methodParameter, resolve(String.class))
      SwaggerParameterBuilder builder = new SwaggerParameterBuilder()
              .withType(paramType)
              .withName('name')
              .withDescription('desc')
              .withModelProvider(modelProvider)
              .withRequired(false)
              .withMethodParameter(resolvedMethodParameter)

    expect:
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

  ResolvedType resolve(Class clazz) {
    asResolved(new TypeResolver(), clazz);
  }
}
