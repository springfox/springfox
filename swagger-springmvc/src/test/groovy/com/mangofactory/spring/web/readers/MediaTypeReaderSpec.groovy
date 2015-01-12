package com.mangofactory.spring.web.readers

import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.SwaggerMediaTypeReader
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Shared

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport])
class MediaTypeReaderSpec extends DocumentationContextSpec {
  MediaTypeReader sut
  @Shared Set<String> emptySet = newHashSet()
  def setup() {
    sut = new MediaTypeReader(defaultValues.typeResolver)
  }

  def "should read media types"() {

    given:
      RequestMappingInfo requestMappingInfo =
            requestMappingInfo('/somePath',
                  [
                        'consumesRequestCondition': consumesRequestCondition(consumes),
                        'producesRequestCondition': producesRequestCondition(produces)
                  ]
            )
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, handlerMethod, 0, requestMappingInfo,
              context(), "")
    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.consumes == newHashSet(consumes)
      operation.produces == newHashSet(produces)

    where:
      consumes                                            | produces                         | handlerMethod
      ['application/json'] as String[]                    | ['application/json'] as String[] | dummyHandlerMethod()
      ['application/json'] as String[]                    | ['application/xml'] as String[]  | dummyHandlerMethod()
      ['multipart/form-data'] as String[]                 | ['application/json'] as String[] | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
      ['application/json', 'application/xml'] as String[] | ['application/xml'] as String[]  | dummyHandlerMethod()
  }

  def "handler method should override spring media types"() {
    RequestMappingInfo requestMappingInfo =
          requestMappingInfo('/somePath',
                [
                      'consumesRequestCondition': consumesRequestCondition(['application/json'] as String[]),
                      'producesRequestCondition': producesRequestCondition(['application/json'] as String[])
                ]
          )
    OperationContext operationContext = new OperationContext(new OperationBuilder(),
            RequestMethod.GET, handlerMethod, 0, requestMappingInfo,
            context(), "")
    when:
      new SwaggerMediaTypeReader().apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.consumes == expectedConsumes
      operation.produces == expectedProduces

    where:
      expectedConsumes                                  | expectedProduces                                  | handlerMethod
      newHashSet('application/xml')                     | newHashSet()                                      | dummyHandlerMethod('methodWithXmlConsumes')
      emptySet                                          | newHashSet('application/xml')                     | dummyHandlerMethod('methodWithXmlProduces')
      newHashSet('application/xml')                     | newHashSet('application/json')                    | dummyHandlerMethod ('methodWithMediaTypeAndFile', MultipartFile)
      newHashSet('application/xml')                     | newHashSet('application/xml')                     | dummyHandlerMethod  ('methodWithBothXmlMediaTypes')
      newHashSet('application/xml', 'application/json') | newHashSet('application/xml', 'application/json') | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
