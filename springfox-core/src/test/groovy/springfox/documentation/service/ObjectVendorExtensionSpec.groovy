package springfox.documentation.service

import spock.lang.Specification

class ObjectVendorExtensionSpec extends Specification {
  def "Object vendor extension adds and retrieves properties" () {
    given:
      ObjectVendorExtension sut = new ObjectVendorExtension("Name")
      StringVendorExtension prop = new StringVendorExtension("A", "B")
    and:
      sut.addProperty(prop)
    expect:
      sut.getName().equals("Name")
      sut.value.size() == 1
      sut.value.first().equals(prop)
  }

  def "Object vendor extension replaces and retrieves properties" () {
    given:
      ObjectVendorExtension sut = new ObjectVendorExtension()
      ObjectVendorExtension other = new ObjectVendorExtension()
      StringVendorExtension prop = new StringVendorExtension("A", "B")
      StringVendorExtension replaced = new StringVendorExtension("A", "C")
    when:
      other.addProperty(replaced)
      sut.addProperty(prop)
      sut.replaceProperty(replaced)
    then:
      sut.value.size() == 1
      replaced.equals(sut.value.first())
    and:
      !sut.equals(prop)
      sut.equals(sut)
      sut.equals(other)
      sut.hashCode() == other.hashCode()
      sut.toString() == other.toString()
  }

  def "Object vendor extension replaces and retrieves properties when not added" () {
    given:
      ObjectVendorExtension sut = new ObjectVendorExtension()
      ObjectVendorExtension other = new ObjectVendorExtension()
      StringVendorExtension prop = new StringVendorExtension("A", "B")
      StringVendorExtension replaced = new StringVendorExtension("A", "C")
    when:
      other.addProperty(replaced)
      sut.replaceProperty(replaced)
    then:
      sut.value.size() == 1
      replaced.equals(sut.value.first())
    and:
      !sut.equals(prop)
      sut.equals(sut)
      sut.equals(other)
      sut.hashCode() == other.hashCode()
      sut.toString() == other.toString()
  }

}
