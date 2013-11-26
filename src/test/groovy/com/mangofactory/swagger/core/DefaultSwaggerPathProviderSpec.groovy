package com.mangofactory.swagger.core

import spock.lang.Specification

import javax.servlet.ServletContext

class DefaultSwaggerPathProviderSpec extends Specification {

   def "Swagger url formats"() {
    given:
      DefaultSwaggerPathProvider defaultSwaggerPathProvider = new DefaultSwaggerPathProvider(apiResourceSuffix: "/api/v1/");
      ServletContext context = Mock(ServletContext)
      context.getContextPath() >> "/context-path"
      defaultSwaggerPathProvider.servletContext = context

    expect:
      defaultSwaggerPathProvider."${method}"() == expected
    where:
      method                 | expected
      "getContextPath"       | "/context-path"
      "getAppBasePath"       | "http://127.0.0.1:8080/context-path"
      "getApiResourcePrefix" | "/api/v1/"
   }
}
