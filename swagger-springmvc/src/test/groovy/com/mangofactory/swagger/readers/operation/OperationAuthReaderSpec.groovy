package com.mangofactory.swagger.readers.operation

import com.mangofactory.service.model.AuthorizationScope
import com.mangofactory.service.model.builder.OperationBuilder
import com.mangofactory.springmvc.plugins.OperationContext
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.core.DocumentationContextSpec
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.plugins.operation.OperationAuthReader
import org.springframework.web.bind.annotation.RequestMethod

@Mixin([RequestMappingSupport, AuthSupport])
class OperationAuthReaderSpec extends DocumentationContextSpec {

  OperationAuthReader sut = new OperationAuthReader()
   def "should read from annotations"(){
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

   def "should apply global auth"(){
    given:

      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
              .withIncludePatterns(['/anyPath.*'])
               .build()
      plugin.authorizationContext(authorizationContext)
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod(), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")


    when:
      sut.apply(operationContext)
      def authorizations = operationContext.operationBuilder().build().authorizations

    then:
      def scopes  = authorizations.get('oauth2')
      AuthorizationScope authorizationScope = scopes[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
   }

   def "should apply global auth when ApiOperationAnnotation exists without auth values"(){
    given:
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
              .withIncludePatterns(['/anyPath.*'])
              .build()
      plugin.authorizationContext(authorizationContext)
      OperationContext operationContext = new OperationContext(new OperationBuilder(),
              RequestMethod.GET, dummyHandlerMethod('methodWithHttpGETMethod'), 0, requestMappingInfo("somePath"),
              context(), "/anyPath")

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
