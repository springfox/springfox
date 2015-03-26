package springfox.documentation.service.model

import spock.lang.Specification
import springfox.documentation.service.ApiListingReference

class ApiListingReferenceSpec extends Specification {
  def "Bean property test" () {
    given:
      def sut = new ApiListingReference("urn:path", "desc", 0);
    expect:
      sut.path == "urn:path"
      sut.description == "desc"
      sut.position == 0
  }
}
