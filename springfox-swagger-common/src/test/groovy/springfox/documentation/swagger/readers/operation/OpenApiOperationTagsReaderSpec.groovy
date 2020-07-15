package springfox.documentation.swagger.readers.operation

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.tags.Tags
import spock.lang.Unroll
import springfox.documentation.spi.service.contexts.OperationContext
import springfox.documentation.spring.web.mixins.RequestMappingSupport
import springfox.documentation.spring.web.plugins.DocumentationContextSpec
import springfox.documentation.spring.web.readers.operation.OperationTagsReader

class OpenApiOperationTagsReaderSpec extends DocumentationContextSpec implements RequestMappingSupport {
  @Unroll
  def "finds tags on #controller.#method correctly"() {
    given:
    OperationContext operationContext =
        operationContext(documentationContext(), dummyHandlerMethodIn(controller, method))
    def sut = new OpenApiOperationTagsReader()

    when:
    sut.apply(operationContext)
    def operation = operationContext.operationBuilder().build()

    then:
    operation.tags == tagNames

    where:
    controller               | method    | tagNames
    FakeControllerWithNoTags | "method0" | [] as Set
    FakeControllerWithNoTags | "method1" | ["tag1"] as Set
    FakeControllerWithNoTags | "method2" | ["tag2"] as Set
    FakeControllerWithNoTags | "method3" | ["tag3", "tag4"] as Set
    FakeController           | "method0" | ["tag5", "tag6"] as Set
    FakeController           | "method1" | ["tag5", "tag6", "tag1"] as Set
    FakeController           | "method2" | ["tag5", "tag6", "tag2"] as Set
    FakeController           | "method3" | ["tag5", "tag6", "tag3", "tag4"] as Set
  }

  class FakeControllerWithNoTags {
    def method0() {
    }

    @Tag(name = "tag1")
    def method1() {
    }

    @Tags(value = [@Tag(name = "tag2")])
    def method2() {
    }

    @Tag(name = "tag4")
    @Tags(value = [@Tag(name = "tag3")])
    def method3() {
    }
  }

  @Tag(name = "tag5")
  @Tags(value = [@Tag(name = "tag6")])
  class FakeController {
    def method0() {
    }

    @Tag(name = "tag1")
    def method1() {
    }

    @Tags(value = [@Tag(name = "tag2")])
    def method2() {
    }

    @Tag(name = "tag4")
    @Tags(value = [@Tag(name = "tag3")])
    def method3() {
    }
  }
}
