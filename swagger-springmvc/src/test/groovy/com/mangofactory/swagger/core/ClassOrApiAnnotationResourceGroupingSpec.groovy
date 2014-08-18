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
      group.position == position
      strategy.getResourceDescription(requestMappingInfo, handlerMethod) == description

    where:
      handlerMethod                                     | groupName     | description   | position
      dummyHandlerMethod()                              | "dummy-class" | "Dummy Class" | 0
      dummyControllerHandlerMethod()                    | "group-name"  | "Group name"  | 2
      dummyControllerWithApiDescriptionHandlerMethod()  | "group-name"  | "Dummy Controller Description"  | 2
  }
}
