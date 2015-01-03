package com.mangofactory.swagger.readers
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
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
      RequestMappingContext requestMappingContext = new RequestMappingContext(context(), requestMappingInfo,
              handlerMethod)
    when:
      sut.execute(requestMappingContext)

    then:
      requestMappingContext.get("consumes") == consumes
      requestMappingContext.get("produces") == produces

    where:
      consumes                                            | produces                         | handlerMethod
      ['application/json'] as String[]                    | ['application/json'] as String[] | dummyHandlerMethod()
      ['application/json'] as String[]                    | ['application/xml'] as String[]  | dummyHandlerMethod()
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
    RequestMappingContext requestMappingContext = new RequestMappingContext(context(), requestMappingInfo,
            handlerMethod)
    when:
      sut.execute(requestMappingContext)

    then:
      requestMappingContext.get("consumes") == expectedConsumes
      requestMappingContext.get("produces") == expectedProduces

    where:
      expectedConsumes                        | expectedProduces                        | handlerMethod
      ['application/xml'] as String[]         | ['application/json'] as String[]        | dummyHandlerMethod('methodWithXmlConsumes')
      ['application/json'] as String[]        | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithXmlProduces')
      ["multipart/form-data"]                 | ['application/json']                    | dummyHandlerMethod('methodWithMediaTypeAndFile', MultipartFile)
      ['application/xml'] as String[]         | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithBothXmlMediaTypes')
      ['application/xml', 'application/json'] | ['application/xml', 'application/json'] | dummyHandlerMethod('methodWithMultipleMediaTypes')

  }
}
