package com.mangofactory.swagger.scanners

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification

class RegexRequestMappingPatternMatcherSpec extends Specification {
   def "matches"() {
    given:
      PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(patternConditions as String[])
      RegexRequestMappingPatternMatcher regexRequestMappingPatternMatcher = new RegexRequestMappingPatternMatcher()
    expect:
      shouldMatch == regexRequestMappingPatternMatcher.patternConditionsMatchOneOfIncluded(patternsRequestCondition, included)

    where:
      patternConditions | included        | shouldMatch
      ['/businesses']   | ['.*?']         | true
      ['/businesses']   | ['/bus.*']      | true
      ['/businesses']   | ['^/bus.*']     | true
      ['/businesses']   | ['/businesses'] | true
      ['/businesses']   | ['/businesses'] | true
      ['/businesses']   | ['/accounts']   | false
      ['/businesses']   | ['/acc.*']      | false
   }


}
