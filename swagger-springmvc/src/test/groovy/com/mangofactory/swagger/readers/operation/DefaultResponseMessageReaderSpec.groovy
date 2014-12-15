package com.mangofactory.swagger.readers.operation
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.models.dto.ResponseMessage
import org.springframework.web.bind.annotation.RequestMethod
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

@Mixin(RequestMappingSupport)
class DefaultResponseMessageReaderSpec extends Specification {

   def "Should add default response messages"() {
    given:
      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
      SwaggerModelsConfiguration modelConfig = new SwaggerModelsConfiguration()
      swaggerGlobalSettings.alternateTypeProvider = modelConfig.alternateTypeProvider(new TypeResolver());
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages())
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), handlerMethod)
      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", currentHttpMethod)
      context.put("responseMessages", newHashSet())
    when:
      DefaultResponseMessageReader operationResponseMessageReader = new DefaultResponseMessageReader()
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
      swaggerGlobalSettings.alternateTypeProvider = new AlternateTypeProvider();
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithApiResponses'))

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", RequestMethod.GET)
      context.put("responseMessages", newHashSet())
    when:
      DefaultResponseMessageReader operationResponseMessageReader = new DefaultResponseMessageReader()
      operationResponseMessageReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseMessages'].size() == 5
      def annotatedResponse = result['responseMessages'].find { it.code == 413 }
      annotatedResponse != null
      annotatedResponse.message == "a message"
   }

   def "Methods with return type containing a model should override the success response code"(){
    given:
      SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
      SpringSwaggerConfig springSwaggerConfig = new SpringSwaggerConfig()
      swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages())
      SwaggerModelsConfiguration modelsConfiguration = new SwaggerModelsConfiguration()
      swaggerGlobalSettings.alternateTypeProvider = modelsConfiguration.alternateTypeProvider(new TypeResolver());
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo('/somePath'), dummyHandlerMethod('methodWithConcreteResponseBody'))

      context.put("swaggerGlobalSettings", swaggerGlobalSettings)
      context.put("currentHttpMethod", RequestMethod.GET)
      context.put("responseMessages", newHashSet())
    when:
      DefaultResponseMessageReader operationResponseMessageReader = new DefaultResponseMessageReader()
      operationResponseMessageReader.execute(context)
      Map<String, Object> result = context.getResult()
      ResponseMessage responseMessage =  result['responseMessages'].find{ it.code == 200 }
    then:
      responseMessage.getCode() == 200
      responseMessage.getResponseModel() == 'BusinessModel'
      responseMessage.getMessage() == "OK"
   }
}
