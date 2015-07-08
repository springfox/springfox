package springfox.documentation.swagger2.mappers

import spock.lang.Specification
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.StringVendorExtension

class VendorExtensionsMapperSpec extends Specification {

  def second() {
    def second = new ObjectVendorExtension("x-test2")
    second.with {
      addProperty(new StringVendorExtension("x-name2", "value2"))
    }
    second
  }

  def first() {
    def first = new ObjectVendorExtension("")
    first.with {
      addProperty(new StringVendorExtension("x-test1", "value1"))
    }
    first
  }

  def "mapper works as expected" () {
    given:
      VendorExtensionsMapper sut = new VendorExtensionsMapper()
    when:
      def mapped = sut.mapExtensions([first(), second()])
    then:
      mapped.containsKey("x-test1")
      mapped["x-test1"] == "value1"
    and:
      mapped.containsKey("x-test2")
      mapped["x-test2"] == ["x-name2": "value2"]
  }
}
