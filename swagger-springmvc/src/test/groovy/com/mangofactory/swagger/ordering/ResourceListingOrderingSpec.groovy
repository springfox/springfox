package com.mangofactory.swagger.ordering

import com.google.common.collect.Ordering
//import com.wordnik.swagger.model.ApiListingReference
import scala.Some
import spock.lang.Specification

//import static com.mangofactory.swagger.ScalaUtils.toOption

class ResourceListingOrderingSpec extends Specification {


//  def "lexicographic order"() {
//    given:
//      Ordering<ApiListingReference> ordering = new ResourceListingLexicographicalOrdering();
//
//    when:
//      Collections.sort(list, ordering)
//    then:
//      list[0].description() == new Some(expectedFirst)
//      list[1].description() == new Some(expectedSecond)
//
//    where:
//      list                                                  | expectedFirst | expectedSecond
//      [apiRef('/b', 'second', 3), apiRef('/a', 'first', 3)] | 'first'       | 'second'
//      [apiRef('/a', 'first', 3), apiRef('/b', 'second', 3)] | 'first'       | 'second'
//  }
//
//  def "positional order"() {
//    given:
//      Ordering<ApiListingReference> ordering = new ResourceListingPositionalOrdering();
//
//    when:
//      Collections.sort(list, ordering)
//    then:
//      list[0].description() == new Some(expectedFirst)
//      list[1].description() == new Some(expectedSecond)
//
//    where:
//      list                                                  | expectedFirst | expectedSecond
//      [apiRef('/b', 'second', 2), apiRef('/a', 'first', 1)] | 'first'       | 'second'
//      [apiRef('/a', 'first', 1), apiRef('/b', 'second', 2)] | 'first'       | 'second'
//  }
//
//  private ApiListingReference apiRef(String path, String desc, Integer pos) {
//    new ApiListingReference(path, toOption(desc), pos)
//  }
}
