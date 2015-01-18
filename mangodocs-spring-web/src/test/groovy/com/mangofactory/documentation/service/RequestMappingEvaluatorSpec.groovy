package com.mangofactory.documentation.service

import com.mangofactory.documentation.service.annotations.ApiIgnore
import com.mangofactory.documentation.spring.web.mixins.AccessorAssertions
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.scanners.RegexRequestMappingPatternMatcher
import spock.lang.Specification

import static com.google.common.collect.Sets.*

@Mixin([AccessorAssertions, RequestMappingSupport])
class RequestMappingEvaluatorSpec extends Specification {
  def "ignore requestMappings "() {
    given:
      RequestMappingEvaluator sut = new RequestMappingEvaluator(new
              RegexRequestMappingPatternMatcher(), newHashSet(ApiIgnore), newHashSet())
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      sut.hasIgnoredAnnotatedRequestMapping(ignorableMethod)
      !sut.shouldIncludeRequestMapping(requestMappingInfo("p"), ignorableMethod)
  }

  def "ignore classMapping"() {
    given:
      RequestMappingEvaluator sut = new RequestMappingEvaluator(new
              RegexRequestMappingPatternMatcher(), newHashSet(ApiIgnore), newHashSet())
      def ignorableClass = ignorableClass()

    expect:
      sut.classHasIgnoredAnnotatedRequestMapping(ignorableClass)
  }

  def "include requestMapping"() {
    given:
      RequestMappingEvaluator apiListingReferenceScanner = new RequestMappingEvaluator(new
              RegexRequestMappingPatternMatcher(), newHashSet(ApiIgnore), newHashSet(patterns))

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
      RequestMappingEvaluator apiListingReferenceScanner = new RequestMappingEvaluator(new
              RegexRequestMappingPatternMatcher(), newHashSet(ApiIgnore), newHashSet(patterns))

    expect:
      sholdInclude == apiListingReferenceScanner.shouldIncludePath(path)

    where:
      path         | patterns    | sholdInclude
      "/some-path" | '.*'        | true
      "/some-path" | '/no-match' | false

  }
}
