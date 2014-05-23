package com.mangofactory.swagger.readers.operation.parameter

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.readers.Command
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.method.HandlerMethod
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

import static com.mangofactory.swagger.models.ResolvedTypes.asResolved

@Mixin(RequestMappingSupport)
class ParameterTypeReaderSpec extends Specification {

  def "param type"() {
    given:
      HandlerMethod handlerMethod = Mock()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      methodParameter.getParameterAnnotations() >> [annotation]
      context.put("methodParameter", methodParameter)

      def settings = new SwaggerGlobalSettings()
      settings.alternateTypeProvider = new AlternateTypeProvider();
      context.put("swaggerGlobalSettings", settings);
      context.put("resolvedMethodParameter", new ResolvedMethodParameter(methodParameter, resolve(type)));

    when:
      Command operationCommand = new ParameterTypeReader()
      operationCommand.execute(context)
    then:
      context.get('paramType') == expected
    where:
      annotation            | type          | expected
      [:] as PathVariable   | Integer       | "path"
      [:] as ModelAttribute | Integer       | "body"
      [:] as RequestHeader  | Integer       | "header"
      [:] as RequestParam   | Integer       | "query"
      null                  | Integer       | "body"
      null                  | MultipartFile | "form"
  }

  ResolvedType resolve(Class clazz) {
    asResolved(new TypeResolver(), clazz);
  }
}
