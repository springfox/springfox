package com.mangofactory.swagger.readers.operation.parameter
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
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

import javax.servlet.ServletContext

import static com.mangofactory.schema.ResolvedTypes.*

@Mixin([RequestMappingSupport,  SpringSwaggerConfigSupport, DocumentationContextSupport])
class ParameterTypeReaderSpec extends Specification {
  DocumentationContext context  = defaultContext(Mock(ServletContext))
  Defaults defaultValues = defaults(Mock(ServletContext))

  def "param type"() {
    given:
      HandlerMethod handlerMethod = Mock()
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"), handlerMethod)
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      methodParameter.getParameterAnnotations() >> [annotation]
      context.put("methodParameter", methodParameter)
      context.put("resolvedMethodParameter", new ResolvedMethodParameter(methodParameter, resolve(type)));

    when:
      Command operationCommand = new ParameterTypeReader(defaultValues.alternateTypeProvider)
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
