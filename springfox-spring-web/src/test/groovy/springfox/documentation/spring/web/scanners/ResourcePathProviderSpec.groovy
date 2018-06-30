package springfox.documentation.spring.web.scanners

import spock.lang.Specification
import spock.lang.Unroll
import springfox.documentation.service.ResourceGroup
import springfox.documentation.spring.web.dummy.DummyClass
import springfox.documentation.spring.web.dummy.DummyClassWithRequestMapping
import springfox.documentation.spring.web.dummy.DummyClassWithRequestMappingWithMultipleMappings
import springfox.documentation.spring.web.dummy.DummyClassWithRequestMappingWithoutForwardSlash
import springfox.documentation.spring.web.dummy.DummyController

class ResourcePathProviderSpec extends Specification {
  @Unroll
  def "Determines resource path for #clazz"() {
    given:
    def sut = new ResourcePathProvider(new ResourceGroup("test", clazz))

    expect:
    path == sut.resourcePath().orElse(null)

    where:
    clazz                                            | path
    null                                             | null
    DummyClass                                       | null
    DummyClassWithRequestMapping                     | "/dummy"
    DummyClassWithRequestMappingWithoutForwardSlash  | "/dummy"
    DummyClassWithRequestMappingWithMultipleMappings | "/dummy"
    DummyController                                  | null
  }
}
