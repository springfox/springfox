package com.mangofactory.documentation.builder

import spock.lang.Specification

import static com.mangofactory.documentation.builder.BuilderDefaults.*

class BuilderDefaultsSpec extends Specification {
  def "BuilderDefaults is a static class" () {
    when:
      new BuilderDefaults()
    then:
      thrown(UnsupportedOperationException)
  }

  def "defaultIfAbsent returns default value if newValue is null" () {
    defaultIfAbsent('newValue', 'oldValue') == 'newValue'
    defaultIfAbsent('newValue', null) == 'newValue'
    defaultIfAbsent(null, 'oldValue') == 'oldValue'
    defaultIfAbsent(null, null) == null
  }

  def "nullToEmptyList transforms null values to empty list" () {
    nullToEmptyList([]).size() == 0
    nullToEmptyList(['string']).size() == 1
    List nullList = null
    nullToEmptyList(nullList).size() == 0
  }
}
