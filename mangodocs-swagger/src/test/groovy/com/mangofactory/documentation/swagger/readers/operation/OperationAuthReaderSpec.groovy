package com.mangofactory.documentation.swagger.readers.operation

import com.mangofactory.documentation.service.RequestMappingPatternMatcher
import com.mangofactory.documentation.service.model.AuthorizationScope
import com.mangofactory.documentation.service.model.builder.OperationBuilder
import com.mangofactory.documentation.spi.service.contexts.AuthorizationContext
import com.mangofactory.documentation.spi.service.contexts.OperationContext
import com.mangofactory.documentation.spring.web.DocumentationContextSpec
import com.mangofactory.documentation.spring.web.mixins.AuthSupport
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import org.springframework.web.bind.annotation.RequestMethod

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport, AuthSupport])
class OperationAuthReaderSpec extends DocumentationContextSpec {

  OperationAuthReader sut = new OperationAuthReader()

  def "should read from annotations"() {
    given:
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithAuth'), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")

    when:
      sut.apply(operationContext)
      def operation = operationContext.operationBuilder().build()

    then:
      operation.authorizations.containsKey("oauth2")
      AuthorizationScope authorizationScope = operation.authorizations.get("oauth2")[0]
      authorizationScope.getDescription() == "scope description"
      authorizationScope.getScope() == "scope"
  }

  def "should apply global auth"() {
    given:
      def patternMatcher = Mock(RequestMappingPatternMatcher)

      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
              .withIncludePatterns(newHashSet('/anyPath.*'))
              .withRequestMappingPatternMatcher(patternMatcher)
              .build()
      plugin.authorizationContext(authorizationContext)
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod(), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")

    and:
      patternMatcher.pathMatchesOneOfIncluded("/anyPath", _) >> true
    when:
      sut.apply(operationContext)
      def authorizations = operationContext.operationBuilder().build().authorizations

    then:
      def scopes = authorizations.get('oauth2')
      AuthorizationScope authorizationScope = scopes[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
  }

  def "should apply global auth when ApiOperationAnnotation exists without auth values"() {
    def patternMatcher = Mock(RequestMappingPatternMatcher)
    given:
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
              .withIncludePatterns(newHashSet('/anyPath.*'))
              .withRequestMappingPatternMatcher(patternMatcher)
              .build()
      plugin.authorizationContext(authorizationContext)
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithHttpGETMethod'), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")
    and:
      patternMatcher.pathMatchesOneOfIncluded("/anyPath", _) >> true
    when:
      sut.apply(operationContext)
      def authorizations = operationContext.operationBuilder().build().authorizations

    then:
      def scopes = authorizations.get("oauth2")
      AuthorizationScope authorizationScope = scopes[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
  }
}
