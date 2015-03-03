package com.mangofactory.documentation.spring.web

import com.mangofactory.documentation.builders.DocumentationBuilder
import spock.lang.Specification

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
