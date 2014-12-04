package com.mangofactory.swagger.core
import com.mangofactory.swagger.annotations.ApiIgnore
import com.mangofactory.swagger.mixins.AccessorAssertions
import com.mangofactory.swagger.mixins.RequestMappingSupport
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin([AccessorAssertions, RequestMappingSupport])
class RequestMappingEvaluatorSpec extends Specification {
  def "ignore requestMappings "() {
    given:
      RequestMappingEvaluator sut = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList())
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      sut.hasIgnoredAnnotatedRequestMapping(ignorableMethod)
      !sut.shouldIncludeRequestMapping(requestMappingInfo("p"), ignorableMethod)
  }

  def "ignore classMapping"() {
    given:
      RequestMappingEvaluator sut = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
            RegexRequestMappingPatternMatcher(), newArrayList())
      def ignorableClass = ignorableClass()

    expect:
      sut.classHasIgnoredAnnotatedRequestMapping(ignorableClass)
  }

  def "include requestMapping"() {
    given:
      RequestMappingEvaluator apiListingReferenceScanner = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(patterns))

    expect:
      sholdInclude == apiListingReferenceScanner.shouldIncludeRequestMapping(requestMapping, handlerMethod)

    where:
      handlerMethod            | requestMapping                   | patterns    | sholdInclude
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '.*'        | true
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '.*'        | false
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '/no-match' | false
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '/no-match' | false

  }

  def "include path"() {
    given:
      RequestMappingEvaluator apiListingReferenceScanner = new RequestMappingEvaluator(newArrayList(ApiIgnore), new
              RegexRequestMappingPatternMatcher(), newArrayList(patterns))

    expect:
      sholdInclude == apiListingReferenceScanner.shouldIncludePath(path)

    where:
      path         | patterns    | sholdInclude
      "/some-path" | '.*'        | true
      "/some-path" | '/no-match' | false

  }
}
