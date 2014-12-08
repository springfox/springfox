package com.mangofactory.swagger.readers.operation

import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.mangofactory.swagger.models.dto.Authorization
import com.mangofactory.swagger.models.dto.AuthorizationScope
import spock.lang.Specification

@Mixin([RequestMappingSupport, AuthSupport])
class OperationAuthReaderSpec extends Specification {

   def "should read from annotations"(){
      given:
      OperationAuthReader authReader = new OperationAuthReader()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), dummyHandlerMethod("methodWithAuth"))
      context.put("requestMappingPattern", "/anyPath")

      when:
      authReader.execute(context)

      then:
      def results = context.get("authorizations")
      Authorization authorization = results[0]
      authorization.getType() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.getScopes()[0]
      authorizationScope.getDescription() == "scope description"
      authorizationScope.getScope() == "scope"
   }

   def "should apply global auth"(){
    given:
      OperationAuthReader authReader = new OperationAuthReader()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), dummyHandlerMethod())
      List<Authorization> authorizations = defaultAuth()
      AuthorizationContext authorizationContext = new AuthorizationContext.AuthorizationContextBuilder(defaultAuth())
              .withIncludePatterns(['/anyPath.*'])
               .build()

      context.put("authorizationContext", authorizationContext)
      context.put("requestMappingPattern", "/anyPath")

    when:
      authReader.execute(context)

    then:
      def results = context.get("authorizations")
      println(results)
      Authorization authorization = results[0]
      authorization.getType() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.getScopes()[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
   }

   def "should apply global auth when ApiOperationAnnotation exists without auth values"(){
    given:
      OperationAuthReader authReader = new OperationAuthReader()
      RequestMappingContext context = new RequestMappingContext(requestMappingInfo("somePath"), dummyHandlerMethod("methodWithHttpGETMethod"))
      List<Authorization> authorizations = defaultAuth()
      AuthorizationContext authorizationContext = new AuthorizationContext.AuthorizationContextBuilder(defaultAuth())
              .withIncludePatterns(['/anyPath.*'])
              .build()

      context.put("authorizationContext", authorizationContext)
      context.put("requestMappingPattern", "/anyPath")

    when:
      authReader.execute(context)

    then:
      def results = context.get("authorizations")
      println(results)
      Authorization authorization = results[0]
      authorization.getType() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.getScopes()[0]
      authorizationScope.getDescription() == "accessEverything"
      authorizationScope.getScope() == "global"
   }
}
