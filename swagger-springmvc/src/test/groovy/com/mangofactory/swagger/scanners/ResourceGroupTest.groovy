package com.mangofactory.swagger.scanners

import spock.lang.Shared
import spock.lang.Specification

class ResourceGroupTest extends Specification {
  @Shared def reference = new ResourceGroup("group", "/")
  def "Equals"() {
    given:

    expect:
      first.equals(second) == expected

    where:
    first                             | second                                | expected
    new ResourceGroup("group", "/")   | null                                  | false
    new ResourceGroup("group", "/")   | new ResourceGroup(null)               | false
    new ResourceGroup("group", "/")   | new ResourceGroup(null, null)         | false
    new ResourceGroup("group", "/")   | new ResourceGroup("group", null)      | false
    new ResourceGroup("group", "/")   | new ResourceGroup("group", "/")       | true
    reference                         | reference                             | true
  }
}
