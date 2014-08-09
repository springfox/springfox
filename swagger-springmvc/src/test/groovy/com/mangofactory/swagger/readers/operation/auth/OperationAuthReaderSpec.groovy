package com.mangofactory.swagger.readers.operation.auth

import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.readers.operation.OperationAuthReader
import com.mangofactory.swagger.scanners.RequestMappingContext
import com.wordnik.swagger.model.Authorization
import com.wordnik.swagger.model.AuthorizationScope
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
      authorization.type() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.scopes()[0]
      authorizationScope.description() == "scope description"
      authorizationScope.scope() == "scope"
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
      authorization.type() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.scopes()[0]
      authorizationScope.description() == "accessEverything"
      authorizationScope.scope() == "global"
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
      authorization.type() == 'oauth2'
      AuthorizationScope authorizationScope = authorization.scopes()[0]
      authorizationScope.description() == "accessEverything"
      authorizationScope.scope() == "global"
   }
}
