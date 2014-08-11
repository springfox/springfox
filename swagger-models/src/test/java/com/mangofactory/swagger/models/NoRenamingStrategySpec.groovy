package com.mangofactory.swagger.models

import spock.lang.Specification

class NoRenamingStrategySpec extends Specification {

  def "rename pascalCase and return same"() {
    given:
      NoRenamingStrategy sut = new NoRenamingStrategy();

    expect:
      sut.name(currentPropertyName) == renamedPropertyName

    where:
      currentPropertyName | renamedPropertyName
      "propertyName"      | "propertyName"
      "name"              | "name"
      "propertyLongName"  | "propertyLongName"
  }
}
