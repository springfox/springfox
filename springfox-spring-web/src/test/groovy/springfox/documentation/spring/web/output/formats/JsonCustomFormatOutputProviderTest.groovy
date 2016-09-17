package springfox.documentation.spring.web.output.formats

import spock.lang.Specification
import springfox.documentation.spring.web.output.RawOutput

/**
 * @author Alexandru-Constantin Bledea
 * @since Sep 17, 2016
 */
class JsonCustomFormatOutputProviderTest extends Specification {

  def "should pass coverage"() {
    expect:
    def provider = new JsonCustomFormatOutputProvider()
    provider.configureMapper()
    provider.getFormat()
  }

}
