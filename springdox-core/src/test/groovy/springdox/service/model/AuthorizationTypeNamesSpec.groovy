package springfox.service.model

import spock.lang.Specification
import springfox.documentation.service.ApiKey
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.OAuth

class AuthorizationTypeNamesSpec extends Specification {
  def "AuthorizationTypes have the correct names" () {
    expect:
      authType.getName() == expectedName
    where:
      authType                                | expectedName  |expectedType
      new ApiKey("api-key", "test", "header") | "api-key"     |"test"
      new OAuth("auth", [], [])               | "auth"        |"oauth2"
      new BasicAuth("basic")                  | "basic"       |"basicAuth"

  }
}
