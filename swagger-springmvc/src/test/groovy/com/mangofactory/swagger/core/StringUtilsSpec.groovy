package com.mangofactory.swagger.core

import spock.lang.Specification

import static com.mangofactory.swagger.core.StringUtils.firstPathSegment
import static com.mangofactory.swagger.core.StringUtils.maybeChompLeadingSlash
import static com.mangofactory.swagger.core.StringUtils.maybeChompTrailingSlash
import static com.mangofactory.swagger.core.StringUtils.splitCamelCase

class StringUtilsSpec extends Specification {
  def "maybe chomping the leading slash works"() {
    expect:
    maybeChompLeadingSlash(input) == expected

    where:
    input           || expected
    "/test"         || "test"
    "test"          || "test"
    "/test/test2"   || "test/test2"
    "/test/test2/"  || "test/test2/"
    "/test/@test2/" || "test/@test2/"
    ""              || ""
    null            || null
  }

  def "maybe chomping the trailing slash works"() {
    expect:
      maybeChompTrailingSlash(input) == expected

    where:
      input           || expected
      "/test"         || "/test"
      "test"          || "test"
      "/test/test2"   || "/test/test2"
      "/test/test2/"  || "/test/test2"
      "/test/@test2/" || "/test/@test2"
      ""              || ""
      null            || null
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
    "/test/@test2/" || "/test"
    "/test@/test2/" || "/test"
    "/tes\nt/test2/"|| "/tes"
    "\n/tes\nt/test2/"|| "\n/tes\nt/test2/"
    ""              || ""
    null            || null
  }

  def "Splitting on camel case"() {
    expect:
      splitCamelCase(input, "-") == expected

    where:
      input           || expected
      "firstSecond"   || "first-Second"
      "FirstSecond"   || "First-Second"
      "FirstNSecond"  || "First-N-Second"
      "First"         || "First"
      ""              || ""
      null            || ""
  }
}
