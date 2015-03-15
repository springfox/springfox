package com.mangofactory.documentation.spring.web.scanners

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification

import static com.google.common.collect.Sets.newHashSet

class RegexRequestMappingPatternMatcherSpec extends Specification {
  def "matches"() {
    given:
      PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(patternConditions as String[])
      RegexRequestMappingPatternMatcher regexRequestMappingPatternMatcher = new RegexRequestMappingPatternMatcher()
    expect:
      shouldMatch == regexRequestMappingPatternMatcher.patternConditionsMatchOneOfIncluded(patternsRequestCondition,
              newHashSet(included))

    where:
      patternConditions | included        | shouldMatch
      ['/businesses']   | ['.*?']         | true
      ['businesses']    | ['.*?']         | true
      ['/businesses']   | ['/bus.*']      | true
      ['/businesses']   | ['^/bus.*']     | true
      ['/businesses']   | ['/businesses'] | true
      ['/businesses']   | ['/businesses'] | true
      ['/businesses']   | ['/accounts']   | false
      ['/businesses']   | ['/acc.*']      | false
  }


}
