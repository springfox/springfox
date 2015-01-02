package com.mangofactory.swagger.readers.operation
import com.mangofactory.service.model.ResponseMessage
import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

import javax.servlet.ServletContext

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport, SpringSwaggerConfigSupport, DocumentationContextSupport])
class DefaultResponseMessageReaderSpec extends Specification {
  DocumentationContext context  = defaultContext(Mock(ServletContext))
  def defaultValues = defaults(Mock(ServletContext))
  DefaultResponseMessageReader sut = new DefaultResponseMessageReader(defaultValues
          .typeResolver, defaultValues.alternateTypeProvider)

   def "Should add default response messages"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo('/somePath'), handlerMethod)
      context.put("currentHttpMethod", currentHttpMethod)
      context.put("responseMessages", newHashSet())
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      def allResponses = result['responseMessages'].collect { it.code }
      assert ecpectedCodes.size() == allResponses.intersect(ecpectedCodes).size()
    where:
      currentHttpMethod | handlerMethod        | ecpectedCodes
      RequestMethod.GET | dummyHandlerMethod() | [200, 404, 403, 401]
   }

   def "swagger annotation should override"() {
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithApiResponses'))

      context.put("currentHttpMethod", RequestMethod.GET)
      context.put("responseMessages", newHashSet())
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseMessages'].size() == 5
      def annotatedResponse = result['responseMessages'].find { it.code == 413 }
      annotatedResponse != null
      annotatedResponse.message == "a message"
   }

   def "Methods with return type containing a model should override the success response code"(){
    given:
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo('/somePath'),
              dummyHandlerMethod('methodWithConcreteResponseBody'))

      context.put("currentHttpMethod", RequestMethod.GET)
      context.put("responseMessages", newHashSet())
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()
      ResponseMessage responseMessage =  result['responseMessages'].find{ it.code == 200 }
    then:
      responseMessage.getCode() == 200
      responseMessage.getResponseModel() == 'BusinessModel'
      responseMessage.getMessage() == "OK"
   }
}
