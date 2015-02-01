package com.mangofactory.documentation.service.model

import spock.lang.Shared
import spock.lang.Specification

class ResourceGroupSpec extends Specification {
  @Shared
  def reference = new ResourceGroup("group", String) //Doesnt matter what the controller class is!

  def "Equals"() {
    expect:
      first.equals(second) == expected
    where:
      first                              | second                             | expected
      new ResourceGroup("group", String) | null                               | false
      new ResourceGroup("group", String) | new ResourceGroup(null, String)    | false
      new ResourceGroup("group", String) | new ResourceGroup(null, Integer)   | false
      new ResourceGroup("group", String) | new ResourceGroup("group", String) | true
      reference                          | reference                          | true
      new ResourceGroup("group", String) | "group"                            | false
  }

  def "Hashcode"() {
    expect:
      first.hashCode().equals(second?.hashCode()) == expected
      first.toString().equals(second?.toString()) == expected
    where:
      first                              | second                             | expected
      new ResourceGroup("group", String) | null                               | false
      new ResourceGroup("group", String) | new ResourceGroup(null, String)    | false
      new ResourceGroup("group", String) | new ResourceGroup(null, Integer)   | false
      new ResourceGroup("group", String) | new ResourceGroup("group", String) | true
      reference                          | reference                          | true
  }

  def "Bean properties work as expected without position constructor"() {
    when:
      def group = new ResourceGroup("group", String)
    then:
      group.controllerClass == String
      group.groupName == "group"
      group.position == 0
  }

  def "Bean properties work as expected with position constructor"() {
    when:
      def group = new ResourceGroup("group", String, 1)
    then:
      group.controllerClass == String
      group.groupName == "group"
      group.position == 1
  }
}
