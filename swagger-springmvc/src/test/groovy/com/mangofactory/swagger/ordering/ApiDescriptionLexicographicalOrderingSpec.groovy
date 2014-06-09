package com.mangofactory.swagger.ordering

import com.google.common.collect.Ordering
import com.wordnik.swagger.model.ApiDescription
import spock.lang.Specification

import static com.mangofactory.swagger.ScalaUtils.emptyScalaList
import static com.mangofactory.swagger.ScalaUtils.toOption

class ApiDescriptionLexicographicalOrderingSpec extends Specification {
  def "positional order"() {
    given:
      Ordering<ApiDescription> ordering = new ApiDescriptionLexicographicalOrdering()

    when:
      println("")
      Collections.sort(list, ordering)
    then:
      list[0].path() == expectedFirst
      list[1].path() == expectedSecond

    where:
      list << [[new ApiDescription("/b", toOption(""), emptyScalaList()),
                new ApiDescription("/a", toOption(""), emptyScalaList())]
      ]
      expectedFirst << ["/a"]
      expectedSecond << ["/b"]
  }
}
