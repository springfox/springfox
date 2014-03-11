package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.core.ModelUtils
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiResponse
import com.wordnik.swagger.annotations.ApiResponses
import com.wordnik.swagger.model.ResponseMessage
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.ScalaUtils.fromOption

@Mixin(RequestMappingSupport)
class OperationResponseMessageReaderSpec extends Specification {

   def "Should add default response messages"() {
    given:
      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages())
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", currentHttpMethod)
    when:
      OperationResponseMessageReader operationResponseMessageReader = new OperationResponseMessageReader()
      operationResponseMessageReader.execute(context)
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
      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages())
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithApiResponses'))

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", RequestMethod.GET)
    when:
      OperationResponseMessageReader operationResponseMessageReader = new OperationResponseMessageReader()
      operationResponseMessageReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseMessages'].size() == 1
      result['responseMessages'][0].code == 413
      result['responseMessages'][0].message == "a message"
   }

   def "Methods with return type containing a model should override the success response code"(){
    given:
      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages())
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithConcreteResponseBody'))

      new ModelUtils()
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", RequestMethod.GET)
    when:
      OperationResponseMessageReader operationResponseMessageReader = new OperationResponseMessageReader()
      operationResponseMessageReader.execute(context)
      Map<String, Object> result = context.getResult()
      ResponseMessage responseMessage =  result['responseMessages'].find{ it.code == 200 }
    then:
      responseMessage.code() == 200
      fromOption(responseMessage.responseModel()) == 'BusinessModel'
      responseMessage.message() == "OK"
   }
}
