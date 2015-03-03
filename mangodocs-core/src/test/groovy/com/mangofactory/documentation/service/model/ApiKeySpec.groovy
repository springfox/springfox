package com.mangofactory.documentation.service.model

import com.mangofactory.documentation.service.ApiKey
import spock.lang.Specification

class ApiKeySpec extends Specification {
  def "Bean properties are set as expected via constructor" () {
    when:
      def apiKey = new ApiKey("mykey", "key1", "header")
    then:
      apiKey.type == "apiKey"
    and:
      apiKey.keyname == "key1"
      apiKey.name == "mykey"
      apiKey.passAs == "header"
  }
}
