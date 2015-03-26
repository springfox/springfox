package springfox.documentation.service.model

import spock.lang.Specification
import springfox.documentation.service.AllowableRangeValues

class AllowableRangeValuesSpec extends Specification {
  def "Bean properties test" () {
    given:
      def sut = new AllowableRangeValues("0", "2")
    expect:
      sut.min == "0"
      sut.max == "2"
  }
}
