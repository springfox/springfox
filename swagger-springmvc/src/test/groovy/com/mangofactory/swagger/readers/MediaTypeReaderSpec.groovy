package com.mangofactory.swagger.readers
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport, SpringSwaggerConfigSupport, DocumentationContextSupport])
class MediaTypeReaderSpec extends Specification {
  Defaults defaultValues
  SwaggerSpringMvcPlugin plugin
  DocumentationContextBuilder contextBuilder
  MediaTypeReader sut

  def setup() {
    defaultValues = defaults(Mock(ServletContext))
    contextBuilder = defaultContextBuilder(defaultValues)
    plugin = new SwaggerSpringMvcPlugin()
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
      RequestMappingContext requestMappingContext = new RequestMappingContext(plugin.build(contextBuilder), requestMappingInfo,
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
    RequestMappingContext requestMappingContext = new RequestMappingContext(plugin.build(contextBuilder), requestMappingInfo,
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
