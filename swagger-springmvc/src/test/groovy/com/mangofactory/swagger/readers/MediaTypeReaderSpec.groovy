package com.mangofactory.swagger.readers
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.SwaggerMediaTypeReader
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

@Mixin([RequestMappingSupport])
class MediaTypeReaderSpec extends DocumentationContextSpec {
  MediaTypeReader sut

  def setup() {
    sut  = new MediaTypeReader(defaultValues.typeResolver)
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
      operation.consumes == consumes
      operation.produces == produces

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
      expectedConsumes                        | expectedProduces                        | handlerMethod
      ['application/xml'] as String[]         | [] as String[]                          | dummyHandlerMethod('methodWithXmlConsumes')
      [] as String[]                          | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithXmlProduces')
      ['application/xml']                     | ['application/json']                    | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
      ['application/xml'] as String[]         | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithBothXmlMediaTypes')
      ['application/xml', 'application/json'] | ['application/xml', 'application/json'] | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
