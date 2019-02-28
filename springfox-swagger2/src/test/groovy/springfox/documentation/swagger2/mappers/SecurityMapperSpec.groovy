package springfox.documentation.swagger2.mappers

import io.swagger.models.auth.ApiKeyAuthDefinition
import io.swagger.models.auth.BasicAuthDefinition
import io.swagger.models.auth.OAuth2Definition
import spock.lang.Specification
import springfox.documentation.builders.ImplicitGrantBuilder
import springfox.documentation.builders.OAuthBuilder
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.GrantType
import springfox.documentation.service.LoginEndpoint
import springfox.documentation.service.ResourceListing

import java.util.stream.Stream

import static java.util.Collections.*
import static java.util.stream.Collectors.*

class SecurityMapperSpec extends Specification {
  def "creates apiKey authentication based on provided security scheme" () {
    given:
      def scheme = new ApiKey("name", "keyName", "header")
    when:
      def sut = new ApiKeyAuthFactory()
    and:
      def mapped = sut.create(scheme)
    then:
      mapped as ApiKeyAuthDefinition
      ((ApiKeyAuthDefinition) mapped).in.toValue() == "header"
      ((ApiKeyAuthDefinition) mapped).name == "keyName"
      ((ApiKeyAuthDefinition) mapped).type == "apiKey"
  }

  def "creates basic authentication based on provided security scheme" () {
    given:
    def scheme = new BasicAuth("name")
    when:
    def sut = new BasicAuthFactory()
    and:
    def mapped = sut.create(scheme)
    then:
    mapped as BasicAuthDefinition
    ((BasicAuthDefinition) mapped).type == "basic"
  }


  def "creates oauth2 authentication based on provided security scheme" () {
    given:
    def scheme =  new OAuthBuilder()
        .grantTypes(grantTypes())
        .scopes(scopes())
        .build()
    when:
    def sut = new OAuth2AuthFactory()
    and:
    def mapped = sut.create(scheme)
    then:
    mapped as OAuth2Definition
    ((OAuth2Definition) mapped).type == "oauth2"
    ((OAuth2Definition) mapped).scopes.containsKey("write:pets")
    ((OAuth2Definition) mapped).scopes.containsKey("read:pets")
    ((OAuth2Definition) mapped).flow == "implicit"
    ((OAuth2Definition) mapped).authorizationUrl == "http://petstore.swagger.io/api/oauth/dialog"
  }

  def "Security mapper handles all types" () {
    given:
      def oauth =  new OAuthBuilder()
          .grantTypes(grantTypes())
          .scopes(scopes())
          .name("oauth2")
          .build()
      def basic = new BasicAuth("basic")
      def apiKey = new ApiKey("apiKey", "keyName", "header")

    when:
      def sut = new SecurityMapper()
      def resourceListing = Mock(ResourceListing)
    and:
      resourceListing.securitySchemes >> [oauth, basic, apiKey]
    and:
      def mapped = sut.toSecuritySchemeDefinitions(resourceListing)
    then:
      mapped.containsKey("oauth2")
      mapped.containsKey("basic")
      mapped.containsKey("apiKey")
      mapped.size() == 3
  }

  List<AuthorizationScope> scopes() {
    Stream.of(new AuthorizationScope("write:pets", "modify pets in your account"),
        new AuthorizationScope("read:pets", "read your pets")).collect(toList())
  }

  List<GrantType> grantTypes() {
    singletonList(new ImplicitGrantBuilder()
        .loginEndpoint(new LoginEndpoint("http://petstore.swagger.io/api/oauth/dialog"))
        .build())
  }
}
