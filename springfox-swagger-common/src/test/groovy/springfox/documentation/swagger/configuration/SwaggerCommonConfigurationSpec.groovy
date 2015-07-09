package springfox.documentation.swagger.configuration

import spock.lang.Specification

class SwaggerCommonConfigurationSpec extends Specification {
  def "For coverage" () {
    expect:
      new SwaggerCommonConfiguration().swaggerProperties() != null
  }
}
