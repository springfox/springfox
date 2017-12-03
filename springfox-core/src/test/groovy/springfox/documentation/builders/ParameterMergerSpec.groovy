package springfox.documentation.builders

import spock.lang.Specification

class ParameterMergerSpec extends Specification {
  def "Merges parameters by name" () {
    given:
      def merger = new ParameterMerger(destination, source)
    when:
      def merged = merger.merged()
      def expected = new HashSet()
      expected.addAll(destination.collect { it.name })
      expected.addAll(source.collect { it.name })
    then:
      merged.size() == expected.size()
    where:
      destination                                 | source
      [param("a", "desc")]                        | [param("a", "desc2")]
      [param("a", "desc")]                        | [param("b", "desc2")]
      [param("a", "desc")]                        | []
      []                                          | [param("a", "desc")]
      []                                          | []
  }

  def param(String name, String desc) {
    new ParameterBuilder()
      .name(name)
      .description(desc)
      .build()
  }
}
