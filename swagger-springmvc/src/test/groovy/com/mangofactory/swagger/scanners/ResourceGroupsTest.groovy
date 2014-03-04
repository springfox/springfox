package com.mangofactory.swagger.scanners

import spock.lang.Shared
import spock.lang.Specification

import static com.mangofactory.swagger.scanners.ResourceGroups.groupUris

class ResourceGroupsTest extends Specification {
  @Shared
  def groups = [new ResourceGroup("group1", "/"), new ResourceGroup("group2", "/")]

  def "GroupUri"() {
    expect:
      groupUris(groupsArray) == uris

    where:
    groupsArray || uris
    []          || []
    groups      || ["group1", "group2"]
  }
}
