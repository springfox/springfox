package com.mangofactory.documentation.service.model

import spock.lang.Specification

class ApiKeySpec extends Specification {
  def "Bean properties are set as expected via constructor" () {
    when:
      def apiKey = new ApiKey("key1", "header")
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "key1"
      apiKey.passAs == "header"
  }
}
