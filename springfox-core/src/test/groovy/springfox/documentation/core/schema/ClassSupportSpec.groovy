package springfox.documentation.core.schema

import spock.lang.Specification
import springfox.documentation.core.schema.ClassSupport

import static springfox.documentation.core.schema.ClassSupport.*

class ClassSupportSpec extends Specification {
  def "Cannot instantiate this class"() {
    when:
      new ClassSupport()
    then:
      thrown(UnsupportedOperationException)
  }

  def "finds classes by name" () {
    given:
      def found = classByName(name)
    expect:
      expectedFound == found.isPresent()
    where:
      name                                | expectedFound
      "java.util.Optional"   | true
      "java.util.NonExistent"             | false
  }

}
