package springfox.documentation.spring.web

import spock.lang.Specification

class SpringMvcDocumentationConfigurationSpec extends Specification {
  def "method coverage test" () {
    expect:
      new SpringMvcDocumentationConfiguration().with {
        defaults()
        resourceGroupCache()
        objectMapperConfigurer()
        operationsCache()
        operationsKeyGenerator()
      }
  }
}
