package com.mangofactory.swagger.core

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

@Mixin(com.mangofactory.swagger.mixins.RequestMappingSupport)
class ClassOrApiAnnotationResourceGroupingSpec extends Specification {

   def "group paths and descriptions"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anything')
      ClassOrApiAnnotationResourceGrouping strategy = new ClassOrApiAnnotationResourceGrouping()

    expect:
      strategy.getResourceGroupPath(requestMappingInfo, handlerMethod) == groupPath
      strategy.getResourceDescription(requestMappingInfo, handlerMethod) == description

    where:
      handlerMethod                  | groupPath                                   | description
      dummyHandlerMethod()           | "com_mangofactory_swagger_dummy_DummyClass" | "com.mangofactory.swagger.dummy.DummyClass"
      dummyControllerHandlerMethod() | "Group+name"                                | "Group name"
   }
}
