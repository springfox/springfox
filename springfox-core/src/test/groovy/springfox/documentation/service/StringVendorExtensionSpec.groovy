package springfox.documentation.service

import spock.lang.Specification

class StringVendorExtensionSpec extends Specification {

  def "String vendor extension stores and retrieves properties" () {
    given:
      StringVendorExtension sut = new StringVendorExtension("A", "B")
    expect:
      sut.name == "A"
      sut.value == "B"
  }

  def "String vendor extension equals works as expected" () {
    given:
      StringVendorExtension sut = new StringVendorExtension("A", "B")
      StringVendorExtension other = new StringVendorExtension("A", "B")
    expect:
      sut.equals(other)
      !sut.equals(new ObjectVendorExtension())
      sut.hashCode() == other.hashCode()
      sut.toString() == other.toString()
  }
}
