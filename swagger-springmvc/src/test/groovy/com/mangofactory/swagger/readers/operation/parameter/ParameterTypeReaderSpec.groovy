package com.mangofactory.swagger.readers.operation.parameter
import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.service.model.builder.ParameterBuilder
import com.mangofactory.springmvc.plugins.ParameterContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.operation.ResolvedMethodParameter
import com.wordnik.swagger.annotations.ApiParam
import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

import static com.mangofactory.schema.ResolvedTypes.*

@Mixin([RequestMappingSupport])
class ParameterTypeReaderSpec extends DocumentationContextSpec {

  def "param type"() {
    given:
      MethodParameter methodParameter = Stub(MethodParameter)
      methodParameter.getParameterAnnotation(ApiParam.class) >> null
      methodParameter.getParameterAnnotations() >> [annotation]
      def resolvedMethodParameter = Mock(ResolvedMethodParameter)
      resolvedMethodParameter.methodParameter >> methodParameter
      resolvedMethodParameter.resolvedParameterType >> resolve(type)
      ParameterContext parameterContext = new ParameterContext(resolvedMethodParameter, new ParameterBuilder(), context())
    when:
      def operationCommand = new ParameterTypeReader(defaultValues.alternateTypeProvider)
      operationCommand.apply(parameterContext)
    then:
      parameterContext.parameterBuilder().build().paramType == expected
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
