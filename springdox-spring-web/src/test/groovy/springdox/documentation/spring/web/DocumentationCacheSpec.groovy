package springdox.documentation.spring.web

import spock.lang.Specification
import springdox.documentation.builders.DocumentationBuilder

class DocumentationCacheSpec extends Specification {
  def "Behaves like a map" () {
    given:
      def sut = new DocumentationCache()
    and:
      sut.addDocumentation(new DocumentationBuilder().name("test").build())

    when:
      def group = sut.documentationByGroup("test")
    then:
      group != null
      group.groupName == "test"
    and:
      sut.documentationByGroup("non-existent") == null

  }
}
