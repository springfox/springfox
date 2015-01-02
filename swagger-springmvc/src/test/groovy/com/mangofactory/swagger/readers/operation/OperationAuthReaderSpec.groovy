package com.mangofactory.swagger.readers.operation

import com.mangofactory.service.model.Authorization
import com.mangofactory.service.model.AuthorizationScope
import com.mangofactory.springmvc.plugin.DocumentationContext
import com.mangofactory.swagger.authorization.AuthorizationContext
import com.mangofactory.swagger.mixins.AuthSupport
import com.mangofactory.swagger.mixins.DocumentationContextSupport
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.mixins.SpringSwaggerConfigSupport
import com.mangofactory.swagger.scanners.RequestMappingContext
import spock.lang.Specification

import javax.servlet.ServletContext

@Mixin([RequestMappingSupport, AuthSupport, SpringSwaggerConfigSupport, DocumentationContextSupport])
class OperationAuthReaderSpec extends Specification {

  DocumentationContext context  = defaultContext(Mock(ServletContext))
   def "should read from annotations"(){
      given:
      OperationAuthReader authReader = new OperationAuthReader()
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"), dummyHandlerMethod
              ("methodWithAuth"))
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
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"),
              dummyHandlerMethod())
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
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
      RequestMappingContext context = new RequestMappingContext(context, requestMappingInfo("somePath"), dummyHandlerMethod
              ("methodWithHttpGETMethod"))
      AuthorizationContext authorizationContext = AuthorizationContext.builder()
              .withAuthorizations(defaultAuth())
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
