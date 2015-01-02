package com.mangofactory.swagger.readers

import com.mangofactory.service.model.Operation
import com.mangofactory.springmvc.plugin.DocumentationContextBuilder
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.controllers.Defaults
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.mangofactory.swagger.readers.operation.DefaultResponseMessageReader
import com.mangofactory.swagger.readers.operation.OperationResponseClassReader
import com.mangofactory.swagger.readers.operation.parameter.OperationParameterReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterDataTypeReader
import com.mangofactory.swagger.readers.operation.parameter.ParameterTypeReader
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher
import com.mangofactory.swagger.scanners.RequestMappingContext
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

import javax.servlet.ServletContext

import static org.springframework.web.bind.annotation.RequestMethod.*

@Mixin([RequestMappingSupport, AuthSupport, SpringSwaggerConfigSupport, DocumentationContextSupport])
class ApiOperationReaderSpec extends Specification {
  Defaults defaultValues
  SwaggerSpringMvcPlugin plugin
  DocumentationContextBuilder contextBuilder
  ApiOperationReader sut

  def setup() {
    defaultValues = defaults(Mock(ServletContext))
    contextBuilder = defaultContextBuilder(defaultValues)
    AuthorizationContext authorizationContext = AuthorizationContext.builder()
            .withAuthorizations(defaultAuth())
            .withRequestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
            .withIncludePatterns([".*"])
            .withRequestMethods(values())
            .build()
    plugin = new SwaggerSpringMvcPlugin().authorizationContext(authorizationContext)
    MediaTypeReader mediaTypeReader = new MediaTypeReader(defaultValues.typeResolver)
    OperationResponseClassReader operationClassReader = new OperationResponseClassReader(defaultValues.typeResolver,
            defaultValues.alternateTypeProvider)
    OperationParameterReader operationParameterReader = new OperationParameterReader(defaultValues.typeResolver,
            defaultValues.alternateTypeProvider,
            new ParameterDataTypeReader(defaultValues.alternateTypeProvider),
            new ParameterTypeReader(defaultValues.alternateTypeProvider))
    DefaultResponseMessageReader defaultMessageReader = new DefaultResponseMessageReader(defaultValues.typeResolver,
            defaultValues.alternateTypeProvider)
    sut = new ApiOperationReader(mediaTypeReader, operationClassReader, operationParameterReader, defaultMessageReader)
  }

  def "Should generate default operation on handler method without swagger annotations"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [
                      patternsRequestCondition      : patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}'),
                      requestMethodsRequestCondition: requestMethodsRequestCondition(PATCH, POST)
              ]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()

      RequestMappingContext context = new RequestMappingContext(plugin.build(contextBuilder),
              requestMappingInfo,
              handlerMethod)
      context.put("requestMappingPattern", "/doesNotMatterForThisTest")
    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      Operation apiOperation = result['operations'][0]
      apiOperation.getMethod() == PATCH.toString()
      apiOperation.getSummary() == handlerMethod.method.name
      apiOperation.getNotes() == handlerMethod.method.name
      apiOperation.getNickname() == handlerMethod.method.name
      apiOperation.getPosition() == 0
      apiOperation.getAuthorizations().size() == 1

      def secondApiOperation = result['operations'][1]
      secondApiOperation.position == 1
  }


  def "Should ignore operations that are marked as hidden"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [
                      patternsRequestCondition      : patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}'),
                      requestMethodsRequestCondition: requestMethodsRequestCondition(PATCH, POST)
              ]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod("methodThatIsHidden")
      RequestMappingContext context = new RequestMappingContext(plugin.build(contextBuilder), requestMappingInfo, handlerMethod)
      context.put("requestMappingPattern", "/doesNotMatterForThisTest") //TODO: Fix this


    when:
      sut.execute(context)
      Map<String, Object> result = context.getResult()

    then:
      0 == result['operations'].size()
  }
}
