package com.mangofactory.swagger.ordering

import com.google.common.collect.Ordering
import com.mangofactory.swagger.models.dto.ApiListingReference
import spock.lang.Specification

class ResourceListingOrderingSpec extends Specification {

  def "lexicographic order"() {
    given:
      Ordering<ApiListingReference> ordering = new ResourceListingLexicographicalOrdering();

    when:
      Collections.sort(list, ordering)
    then:
      list[0].getDescription() == expectedFirst
      list[1].getDescription() == expectedSecond

    where:
      list                                                  | expectedFirst | expectedSecond
      [apiRef('/b', 'second', 3), apiRef('/a', 'first', 3)] | 'first'       | 'second'
      [apiRef('/a', 'first', 3), apiRef('/b', 'second', 3)] | 'first'       | 'second'
  }

  def "positional order"() {
    given:
      Ordering<ApiListingReference> ordering = new ResourceListingPositionalOrdering();

    when:
      Collections.sort(list, ordering)
    then:
      list[0].getDescription() == expectedFirst
      list[1].getDescription() == expectedSecond

    where:
      list                                                  | expectedFirst | expectedSecond
      [apiRef('/b', 'second', 2), apiRef('/a', 'first', 1)] | 'first'       | 'second'
      [apiRef('/a', 'first', 1), apiRef('/b', 'second', 2)] | 'first'       | 'second'
  }

  private ApiListingReference apiRef(String path, String desc, Integer pos) {
    new ApiListingReference(path, desc, pos)
  }
}
