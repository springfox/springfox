package springfox.documentation.swagger.web

import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.spring.web.mixins.RequestMappingSupport

@Mixin(RequestMappingSupport)
class ClassOrApiAnnotationResourceGroupingSpec extends Specification {

  @Unroll
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
      handlerMethod                                    | groupName              | description                    | position
      dummyHandlerMethod()                             | "dummy-class"          | "Dummy Class"                  | 0
      dummyControllerHandlerMethod()                   | "group-name"           | "Group name"                   | 2
      dummyControllerWithApiDescriptionHandlerMethod() | "group-name"           | "Dummy Controller Description" | 2
      petServiceHandlerMethod()                        | "pet-service"          | "Operations about pets"        | 0
      multipleRequestMappingsHandlerMethod()           | "pet-grooming-service" | "Grooming operations for pets" | 0


  }
}
