package springdox.documentation.schema

import spock.lang.Specification

class CodeGenGenericTypeNamingStrategySpec extends Specification {
  def "Verify open close and delimiter" () {
    given:
      def sut = new CodeGenGenericTypeNamingStrategy()
    expect:
      sut.openGeneric == "Of"
      sut.closeGeneric == ""
      sut.typeListDelimiter == "And"
  }
}
