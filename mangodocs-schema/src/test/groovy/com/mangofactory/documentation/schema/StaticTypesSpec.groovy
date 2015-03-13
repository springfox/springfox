package com.mangofactory.documentation.schema

import spock.lang.Specification

class StaticTypesSpec extends Specification {
  def "Static types cannot be instantiated" () {
    when:
      clazz.newInstance();
    then:
      thrown(UnsupportedOperationException)
    where:
      clazz << [ResolvedTypes, Types, Enums, Annotations, Collections]
  }
}
