package com.mangofactory.swagger.core

import com.mangofactory.swagger.mixins.RequestMappingSupport
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification

@Mixin(RequestMappingSupport)
class ClassOrApiAnnotationResourceGroupingSpec extends Specification {

   def "group paths and descriptions"() {
    given:
      RequestMappingInfo requestMappingInfo = requestMappingInfo('/anything')
      ClassOrApiAnnotationResourceGrouping strategy = new ClassOrApiAnnotationResourceGrouping()
      def group = strategy.getResourceGroups(requestMappingInfo, handlerMethod).first()


     expect:
      group.groupName == groupName
      group.realUri == realPath
      strategy.getResourceDescription(requestMappingInfo, handlerMethod) == description

    where:
      handlerMethod                  | groupName                                   | realPath                                     | description
      dummyHandlerMethod()           | "com_mangofactory_swagger_dummy_DummyClass" | "/com_mangofactory_swagger_dummy_DummyClass" |"com.mangofactory.swagger.dummy.DummyClass"
      dummyControllerHandlerMethod() | "Group+name"                                | "/Group+name"                                | "Group name"
   }
}
