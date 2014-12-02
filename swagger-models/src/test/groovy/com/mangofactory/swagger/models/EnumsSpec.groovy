package com.mangofactory.swagger.models

import com.wordnik.swagger.model.AllowableListValues
import scala.collection.JavaConversions
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

class EnumsSpec extends Specification {
  def "enums support @JsonValue annotation"() {
    given:
      def expected = new AllowableListValues(JavaConversions.collectionAsScalaIterable(newArrayList("One", "Two"))
              .toList(), "LIST")
    expect:
      expected == Enums.allowableValues(JsonValuedEnum)

  }

  def "enums support regular enums"() {
    given:
      def expected = new AllowableListValues(JavaConversions.collectionAsScalaIterable(newArrayList("ONE", "TWO"))
              .toList(), "LIST")
    expect:
      expected == Enums.allowableValues(ExampleEnum)
  }

  def "enums work with incorrectly annotated enums"() {
    given:
      def expected = new AllowableListValues(JavaConversions.collectionAsScalaIterable(newArrayList("ONE", "TWO"))
              .toList(), "LIST")
    expect:
      expected == Enums.allowableValues(IncorrectlyJsonValuedEnum)
  }

  def "Enums class in not instantiable"() {
    when:
      new Enums()
    then:
      thrown(UnsupportedOperationException)
  }
}
