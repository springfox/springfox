package com.mangofactory.spring.web.readers
import com.mangofactory.service.model.Operation
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.spring.web.plugins.DocumentationPluginsManager
import com.mangofactory.spring.web.plugins.AuthorizationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.PluginsSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.spring.web.scanners.RegexRequestMappingPatternMatcher
import com.mangofactory.spring.web.scanners.RequestMappingContext
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Ignore

import static org.springframework.web.bind.annotation.RequestMethod.*

@Mixin([RequestMappingSupport, AuthSupport, PluginsSupport])
class ApiOperationReaderSpec extends DocumentationContextSpec {
  ApiOperationReader sut

  def setup() {
    AuthorizationContext authorizationContext = AuthorizationContext.builder()
            .withAuthorizations(defaultAuth())
            .withRequestMappingPatternMatcher(new RegexRequestMappingPatternMatcher())
            .withIncludePatterns([".*"])
            .withRequestMethods(values())
            .build()
    plugin.authorizationContext(authorizationContext)
    sut = new ApiOperationReader(springPluginsManager())
  }

  @Ignore("This is really an integration test")
  def "Should generate default operation on handler method without swagger annotations"() {

    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo("/doesNotMatterForThisTest",
              [
                      patternsRequestCondition      : patternsRequestCondition('/doesNotMatterForThisTest', '/somePath/{businessId:\\d+}'),
                      requestMethodsRequestCondition: requestMethodsRequestCondition(PATCH, POST)
              ]
      )

      HandlerMethod handlerMethod = dummyHandlerMethod()

      RequestMappingContext context = new RequestMappingContext(context(),
              requestMappingInfo,
              handlerMethod)
    when:
      def operations = sut.read(context)

    then:
      Operation apiOperation = operations[0]
      apiOperation.getMethod() == PATCH.toString()
      apiOperation.getSummary() == handlerMethod.method.name
      apiOperation.getNotes() == handlerMethod.method.name
      apiOperation.getNickname() == handlerMethod.method.name
      apiOperation.getPosition() == 0
      apiOperation.getAuthorizations().size() == 1

      def secondApiOperation = operations[1]
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
      RequestMappingContext context = new RequestMappingContext(context(), requestMappingInfo, handlerMethod)

    when:
      def mock = Mock(DocumentationPluginsManager)
      mock.operation(_) >> new OperationBuilder().hidden(true).build()
      def operations = new ApiOperationReader(mock).read(context)

    then:
      0 == operations.size()
  }
}
