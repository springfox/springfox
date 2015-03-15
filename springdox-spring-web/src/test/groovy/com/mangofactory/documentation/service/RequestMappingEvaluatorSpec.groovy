package com.mangofactory.documentation.service
import com.mangofactory.documentation.annotations.ApiIgnore
import com.mangofactory.documentation.spring.web.SpringRequestMappingEvaluator
import com.mangofactory.documentation.spring.web.mixins.RequestMappingSupport
import com.mangofactory.documentation.spring.web.scanners.RegexRequestMappingPatternMatcher
import spock.lang.Specification

import static com.google.common.collect.Sets.*

@Mixin([RequestMappingSupport])
class RequestMappingEvaluatorSpec extends Specification {
  def "ignore requestMappings "() {
    given:
      SpringRequestMappingEvaluator sut = new SpringRequestMappingEvaluator(new RegexRequestMappingPatternMatcher())
      sut.appendExcludeAnnotations(newHashSet(ApiIgnore))
      def ignorableMethod = ignorableHandlerMethod()

    expect:
      sut.hasIgnoredAnnotatedRequestMapping(ignorableMethod)
      !sut.shouldIncludeRequestMapping(requestMappingInfo("p"), ignorableMethod)
  }

  def "ignore classMapping"() {
    given:
      SpringRequestMappingEvaluator sut = new SpringRequestMappingEvaluator(new RegexRequestMappingPatternMatcher())
      sut.appendExcludeAnnotations(newHashSet(ApiIgnore))
      def ignorableClass = ignorableClass()

    expect:
      sut.classHasIgnoredAnnotatedRequestMapping(ignorableClass)
  }

  def "include requestMapping"() {
    given:
      SpringRequestMappingEvaluator evaluator = new SpringRequestMappingEvaluator(new RegexRequestMappingPatternMatcher())
      evaluator.appendExcludeAnnotations(newHashSet(ApiIgnore))
      evaluator.appendIncludePatterns(newHashSet(patterns))

    expect:
      sholdInclude == evaluator.shouldIncludeRequestMapping(requestMapping, handlerMethod)

    where:
      handlerMethod            | requestMapping                   | patterns    | sholdInclude
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '.*'        | true
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '.*'        | false
      dummyHandlerMethod()     | requestMappingInfo("/some-path") | '/no-match' | false
      ignorableHandlerMethod() | requestMappingInfo("/some-path") | '/no-match' | false

  }

  def "include path"() {
    given:
      SpringRequestMappingEvaluator evaluator = new SpringRequestMappingEvaluator(new RegexRequestMappingPatternMatcher())
      evaluator.appendExcludeAnnotations(newHashSet(ApiIgnore))
      evaluator.appendIncludePatterns(newHashSet(patterns))

    expect:
      sholdInclude == evaluator.shouldIncludePath(path)

    where:
      path         | patterns    | sholdInclude
      "/some-path" | '.*'        | true
      "/some-path" | '/no-match' | false

  }
}
