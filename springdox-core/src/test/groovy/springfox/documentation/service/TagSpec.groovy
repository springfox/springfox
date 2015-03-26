package springfox.documentation.service

import spock.lang.Specification

class TagSpec extends Specification {
  Tag tag = new Tag("pet", "Pet tag")
  
  def "should pass coverage"() {
    expect:
      tag.with {
        getName()
        getDescription()
      }
  }
}
