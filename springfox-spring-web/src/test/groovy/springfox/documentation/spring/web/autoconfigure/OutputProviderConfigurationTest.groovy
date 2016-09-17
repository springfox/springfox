package springfox.documentation.spring.web.autoconfigure

import spock.lang.Specification

/**
 * @author Alexandru-Constantin Bledea
 * @since Sep 17, 2016
 */
class OutputProviderConfigurationTest extends Specification{

  def "should pass coverage"() {
    expect:
    new OutputProviderConfiguration().yamlCustomFormatOutputProvider()
  }

}
