package springdox.service.model

import spock.lang.Specification
import springdox.documentation.service.ApiKey
import springdox.documentation.service.BasicAuth
import springdox.documentation.service.OAuth

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
