package com.mangofactory.documentation.swagger.readers.operation

import com.mangofactory.documentation.service.model.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.plugins.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport])
class SwaggerMediaTypeReaderSpec extends DocumentationContextSpec {
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
      newHashSet()                                      | newHashSet('application/xml')                     |  dummyHandlerMethod('methodWithXmlProduces')
      newHashSet('application/xml')                     | newHashSet('application/json')                    | dummyHandlerMethod ('methodWithMediaTypeAndFile', MultipartFile)
      newHashSet('application/xml')                     | newHashSet('application/xml')                     | dummyHandlerMethod  ('methodWithBothXmlMediaTypes')
      newHashSet('application/xml', 'application/json') | newHashSet('application/xml', 'application/json') | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
