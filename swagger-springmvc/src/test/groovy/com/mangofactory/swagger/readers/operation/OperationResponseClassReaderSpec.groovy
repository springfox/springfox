package com.mangofactory.swagger.readers.operation

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.alternates.WildcardType
import com.mangofactory.swagger.models.configuration.SwaggerModelsConfiguration
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

import static com.mangofactory.swagger.models.alternates.Alternates.newRule

@Mixin(RequestMappingSupport)
class OperationResponseClassReaderSpec extends Specification {

  @Unroll
   def "should have correct response class"() {
    given:
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      def settings = new SwaggerGlobalSettings()
      SwaggerModelsConfiguration springSwaggerConfig = new SwaggerModelsConfiguration()

      def typeResolver = new TypeResolver()
      settings.alternateTypeProvider = springSwaggerConfig.alternateTypeProvider(typeResolver)

      setupSpringDefaults(settings, typeResolver)

      context.put("swaggerGlobalSettings", settings)
      OperationResponseClassReader operationResponseClassReader = new OperationResponseClassReader()

    when:
      operationResponseClassReader.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      result['responseClass'] == expectedClass
    where:
      handlerMethod                                                        | expectedClass
      dummyHandlerMethod('methodWithConcreteResponseBody')                 | 'BusinessModel'
      dummyHandlerMethod('methodApiResponseClass')                         | 'FunkyBusiness'
      dummyHandlerMethod('methodWithAPiAnnotationButWithoutResponseClass') | 'FunkyBusiness'
      dummyHandlerMethod('methodWithGenericType')                          | 'Paginated«string»'
      dummyHandlerMethod('methodWithGenericPrimitiveArray')                | 'Array[byte]'
      dummyHandlerMethod('methodWithGenericComplexArray')                  | 'Array[DummyClass]'

   }

  private void setupSpringDefaults(SwaggerGlobalSettings settings, TypeResolver typeResolver) {
    settings.alternateTypeProvider.addRule(newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));

    settings.alternateTypeProvider.addRule(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));
  }
}
