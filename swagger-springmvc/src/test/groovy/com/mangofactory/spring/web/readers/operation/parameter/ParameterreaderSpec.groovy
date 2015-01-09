package com.mangofactory.spring.web.readers.operation.parameter

import com.mangofactory.service.model.builder.ParameterBuilder
import com.mangofactory.spring.web.plugins.ParameterContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.ModelProviderForServiceSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.parameter.ParameterAccessReader
import com.mangofactory.swagger.plugins.operation.parameter.ParameterDescriptionReader
import com.mangofactory.spring.web.readers.operation.ResolvedMethodParameter
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestParam
import spock.lang.Unroll

@Mixin([RequestMappingSupport, ModelProviderForServiceSupport])
class ParameterReaderSpec extends DocumentationContextSpec {
   @Unroll("property #resultProperty expected: #expected")
   def "should set basic properties based on ApiParam annotation or a sensible default"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> apiParamAnnotation
      methodParameter.getParameterAnnotation(RequestParam.class) >> reqParamAnnot
      methodParameter.getParameterAnnotations() >> [apiParamAnnotation, reqParamAnnot]
      methodParameter."$springParameterMethod"() >> methodReturnValue
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
    when:
      parameterPlugin.apply(parameterContext)

    then:
      parameterContext.parameterBuilder().build()."$resultProperty" == expected
    where:
      parameterPlugin                     | resultProperty | springParameterMethod | methodReturnValue | apiParamAnnotation                     | reqParamAnnot                          | expected
      new ParameterNameReader()           | 'name'         | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | apiParam ([name: {-> 'AnName' }])      | null                                   | 'param0'
      new ParameterNameReader()           | 'name'         | 'none'                | 'any'             | null                                   | reqParam([value: {-> 'ArName' }])      | 'ArName'
      new ParameterDescriptionReader()    | 'description'  | 'getParameterName'    | 'someName'        | null                                   | null                                   | 'someName'
      new ParameterDescriptionReader()    | 'description'  | 'none'                | 'any'             | apiParam([value: {-> 'AnDesc' }])      | null                                   | 'AnDesc'
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | null                                   | ''
      swaggerDefaultReader()              | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: {-> 'defl' }]) | null                                   | 'defl'
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | apiParam([defaultValue: {-> 'defl' }]) | null                                   | ''
      new ParameterDefaultReader()        | 'defaultValue' | 'none'                | 'any'             | null                                   | reqParam([defaultValue: {-> 'defr' }]) | 'defr'
      new ParameterAccessReader()         | 'paramAccess'  | 'none'                | 'any'             | apiParam([access: {-> 'myAccess' }])   | null                                   | 'myAccess'
   }

  com.mangofactory.swagger.plugins.operation.parameter.ParameterNameReader swaggerParameterNameReader() {
    return new com.mangofactory.swagger.plugins.operation.parameter.ParameterNameReader()
  }

  com.mangofactory.swagger.plugins.operation.parameter.ParameterDefaultReader swaggerDefaultReader() {
    new com.mangofactory.swagger.plugins.operation.parameter.ParameterDefaultReader()
  }


  private ApiParam apiParam(Map closureMap) {
      closureMap as ApiParam
   }

   private RequestParam reqParam(Map closureMap) {
      closureMap as RequestParam
   }
}
