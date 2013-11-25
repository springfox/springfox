package com.mangofactory.swagger.readers

import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll

@Mixin(RequestMappingSupport)
class MediaTypeReaderSpec extends Specification {

   @Unroll
   def "should read media types"() {

    given:
      MediaTypeReader mediaTypeReader = new MediaTypeReader()
      RequestMappingInfo requestMappingInfo =
         requestMappingInfo('/somePath',
                 [
                         'consumesRequestCondition': consumesRequestCondition(consumes),
                         'producesRequestCondition': producesRequestCondition(produces)
                 ]
         )
      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, handlerMethod)
    when:
      mediaTypeReader.execute(requestMappingContext)

    then:
      requestMappingContext.get("consumes") == consumes
      requestMappingContext.get("produces") == produces

    where:
      consumes                                            | produces                         | handlerMethod
      ['application/json'] as String[]                    | ['application/json'] as String[] | dummyHandlerMethod()
      ['application/json'] as String[]                    | ['application/xml'] as String[]  | dummyHandlerMethod()
      ['application/json', 'application/xml'] as String[] | ['application/xml'] as String[]  | dummyHandlerMethod()
   }

   @Unroll
   def "handler method should override spring media types"() {
      MediaTypeReader mediaTypeReader = new MediaTypeReader()
      RequestMappingInfo requestMappingInfo =
         requestMappingInfo('/somePath',
                 [
                         'consumesRequestCondition': consumesRequestCondition(['application/json'] as String[]),
                         'producesRequestCondition': producesRequestCondition(['application/json'] as String[])
                 ]
         )
      RequestMappingContext requestMappingContext = new RequestMappingContext(requestMappingInfo, handlerMethod)

    when:
      mediaTypeReader.execute(requestMappingContext)

    then:
      requestMappingContext.get("consumes") == expectedConsumes
      requestMappingContext.get("produces") == expectedProduces

    where:
      expectedConsumes                        | expectedProduces                        | handlerMethod
      ['application/xml'] as String[]         | ['application/json'] as String[]        | dummyHandlerMethod('methodWithXmlConsumes')
      ['application/json'] as String[]        | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithXmlProduces')
      ['application/xml'] as String[]         | ['application/xml'] as String[]         | dummyHandlerMethod('methodWithBothXmlMediaTypes')
      ['application/xml', 'application/json'] | ['application/xml', 'application/json'] | dummyHandlerMethod('methodWithMultipleMediaTypes')
   }
}
