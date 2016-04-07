package springfox.documentation.schema

import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import spock.lang.Specification

import static springfox.documentation.schema.ClassSupport.*

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
      "com.google.common.base.Optional"   | true
      "java.util.NonExistent"             | false
  }

  def "detects optional types" () {
    given:
      def resolver = new TypeResolver()
      def optional = isOptional(resolver.resolve(type))
    expect:
      expectedOptional == optional
    where:
      type     | expectedOptional
      Optional | true
      String   | false
  }

}
