package springfox.documentation.spring.web.output.formats

import spock.lang.Specification

/**
 * @author Alexandru-Constantin Bledea
 * @since Sep 17, 2016
 */
class YamlCustomFormatOutputProviderTest extends Specification {

  def "should pass coverage"() {
    expect:
    def provider = new YamlCustomFormatOutputProvider()
    provider.configureMapper()
    provider.getFormats()
  }

}
