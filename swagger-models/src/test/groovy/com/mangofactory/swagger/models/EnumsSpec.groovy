package com.mangofactory.swagger.models

import com.mangofactory.swagger.models.dto.AllowableListValues
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

class EnumsSpec extends Specification {
  def "enums support @JsonValue annotation"() {
    given:
      def expected = new AllowableListValues(newArrayList("One", "Two"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(JsonValuedEnum).getValues()

  }

  def "enums support regular enums"() {
    given:
      def expected = new AllowableListValues(newArrayList("ONE", "TWO"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(ExampleEnum).getValues()
  }

  def "enums work with incorrectly annotated enums"() {
    given:
      def expected = new AllowableListValues(newArrayList("ONE", "TWO"), "LIST")
    expect:
      expected.getValues() == Enums.allowableValues(IncorrectlyJsonValuedEnum).getValues()
  }

  def "Enums class in not instantiable"() {
    when:
      new Enums()
    then:
      thrown(UnsupportedOperationException)
  }
}
