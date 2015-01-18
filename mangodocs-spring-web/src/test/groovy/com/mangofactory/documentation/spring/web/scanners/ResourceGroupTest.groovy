package com.mangofactory.documentation.spring.web.scanners

import com.mangofactory.documentation.service.model.ResourceGroup
import com.mangofactory.documentation.spring.web.dummy.DummyClass
import com.mangofactory.documentation.spring.web.dummy.DummyController
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
