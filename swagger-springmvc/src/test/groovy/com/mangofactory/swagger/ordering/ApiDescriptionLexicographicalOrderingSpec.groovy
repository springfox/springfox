package com.mangofactory.swagger.ordering

import com.google.common.collect.Ordering
import com.mangofactory.swagger.models.dto.ApiDescription
import spock.lang.Specification


class ApiDescriptionLexicographicalOrderingSpec extends Specification {
  def "positional order"() {
    given:
      Ordering<ApiDescription> ordering = new ApiDescriptionLexicographicalOrdering()

    when:
      println("")
      Collections.sort(list, ordering)
    then:
      list[0].getPath() == expectedFirst
      list[1].getPath() == expectedSecond

    where:
      list << [[new ApiDescription("/b", "", [], false),
                new ApiDescription("/a", "", [], false)]
      ]
      expectedFirst << ["/a"]
      expectedSecond << ["/b"]
  }
}
