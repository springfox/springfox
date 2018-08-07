package springfox.documentation.spring.web

import spock.lang.Specification

class SpringfoxWebMvcConfigurationSpec extends Specification {
  def "method coverage test" () {
    given:
      def config = new SpringfoxWebConfiguration()
    expect:
      config.with {
        defaults()
        resourceGroupCache()
        jsonSerializer([])
      }
  }
}
