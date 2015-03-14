package com.mangofactory.documentation.schema

import spock.lang.Specification
import spock.lang.Unroll

class ModelRefSpec extends Specification {
  @Unroll
  def "map types are reflected correctly" () {
    expect:
      model.isCollection() == isCollection
      model.isMap() == isMap
    where:
      model                                   | isCollection  | isMap
      new ModelRef("string")                  | false         | false
      new ModelRef("string", null)            | false         | false
      new ModelRef("string", null, true)      | false         | false
      new ModelRef("string", "List", true)    | false         | true
      new ModelRef("string", "List", false)   | true          | false
      new ModelRef("string", "Map", true)     | false         | true
      new ModelRef("string", "Map", false)    | true          | false
  }
}
