package com.mangofactory.schema

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification

import static com.mangofactory.schema.Collections.collectionElementType

class CollectionsSpec extends Specification {
  def "Container type throws exception when its passed a non-iterable type"() {
    when:
      Collections.containerType(new TypeResolver().resolve(ExampleEnum))
    then:
      thrown(UnsupportedOperationException)
  }

  def "Container element type is null when its passed a non-iterable type"() {
    when:
      def type  = collectionElementType(new TypeResolver().resolve(ExampleEnum))
    then:
      type == null
  }
}
