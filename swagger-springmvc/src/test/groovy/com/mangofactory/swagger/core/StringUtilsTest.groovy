package com.mangofactory.swagger.core

import spock.lang.Specification

import static com.mangofactory.swagger.core.StringUtils.firstPathSegment
import static com.mangofactory.swagger.core.StringUtils.maybeChompLeadingSlash

class StringUtilsTest extends Specification {
  def "maybe chomping the leading slash works"() {
    expect:
    maybeChompLeadingSlash(input) == expected

    where:
    input         || expected
    "/test"       || "test"
    "test"        || "test"
    "/test/test2" || "test/test2"
    ""            || ""
    null          || null
  }


  def "extracting first path segment"() {
    expect:
    firstPathSegment(input) == expected

    where:
    input           || expected
    "/test"         || "/test"
    "test"          || "test"
    "/test/test2"   || "/test"
    "/test/test2/"  || "/test"
    ""              || ""
    null            || null
  }
}
