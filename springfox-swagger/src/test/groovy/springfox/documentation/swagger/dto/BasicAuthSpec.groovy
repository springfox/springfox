package springfox.documentation.swagger.dto

class BasicAuthSpec extends InternalJsonSerializationSpec {


  def "should serialize"() {
    given:
      AuthorizationType basicAuth = new BasicAuth()
    when:
      basicAuth.setName("basic")
    then:
      writePretty(basicAuth) == """{
  "name" : "basic",
  "type" : "basicAuth"
}"""
  }

  def "should pass coverage"() {
    given:
      AuthorizationType basicAuth = new BasicAuth()
    when:
      basicAuth.setName("basic")
    then:
      basicAuth.getName() == 'basic'
      basicAuth.getType() == 'basicAuth'
  }
}
