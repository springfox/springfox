package com.mangofactory.spring.web.scanners

import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import spock.lang.Specification

class AntRequestMappingPatternMatcherSpec extends Specification {

   def "matches"() {
    given:
      PatternsRequestCondition patternsRequestCondition = new PatternsRequestCondition(patternConditions as String[])
      AntRequestMappingPatternMatcher antRequestMappingPatternMatcher = new AntRequestMappingPatternMatcher()
    expect:
      shouldMatch == antRequestMappingPatternMatcher.patternConditionsMatchOneOfIncluded(patternsRequestCondition, included)

    where:
      included                 | patternConditions                | shouldMatch
      ['']                     | ['path']                         | false
      ['sdfs']                 | ['path']                         | false
      ['/path']                | ['path']                         | true
      ['/?ath']                | ['path']                         | true
      ['/*ath']                | ['path']                         | true
      ['/**path']              | ['path']                         | true
      ['/path/']               | ['path']                         | false
      ['/**path']              | ['path']                         | true
      ['/**']                  | ['path']                         | true
      ['/businesses/accounts'] | ['/businesses/accounts']         | true
      ['/**']                  | ['/businesses/accounts']         | true
      ['/*/accounts']          | ['/businesses/accounts']         | true
      ['/*/accounts']          | ['/businesses/accounts']         | true
      ['/**/accounts']         | ['/businesses/accounts']         | true
      ['/**businesses/**']     | ['/businesses/accounts']         | true
      ['/**']                  | ['/businesses/accounts/balance'] | true
      ['/path', '/?ath']       | ['path']                         | true
      ['/path', '/notAMatch']  | ['path']                         | true
      ['/both', '/doNotMatch'] | ['path']                         | false
   }
}
