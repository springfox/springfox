package com.mangofactory.spring.web.scanners

import com.mangofactory.swagger.dummy.DummyClass
import com.mangofactory.swagger.dummy.DummyController
import spock.lang.Shared
import spock.lang.Specification

class ResourceGroupTest extends Specification {
   @Shared
   def reference = new ResourceGroup("group", DummyClass)

   def "Equals"() {
      expect:
        first.equals(second) == expected
        first.position == 0
      where:
        first                                  | second                                   | expected
        new ResourceGroup("group", DummyClass) | null                                     | false
        new ResourceGroup("group", DummyClass) | new ResourceGroup(null, DummyClass)      | false
        new ResourceGroup("group", DummyClass) | new ResourceGroup(null, DummyController) | false
        new ResourceGroup("group", DummyClass) | new ResourceGroup("group", DummyClass)   | true
        reference                              | reference                                | true
   }
}
