package springdox.documentation.swagger.mappers

import spock.lang.Specification
import springdox.documentation.builders.AuthorizationBuilder
import springdox.documentation.builders.AuthorizationCodeGrantBuilder
import springdox.documentation.builders.AuthorizationScopeBuilder
import springdox.documentation.builders.ImplicitGrantBuilder
import springdox.documentation.builders.LoginEndpointBuilder
import springdox.documentation.builders.OAuthBuilder
import springdox.documentation.builders.TokenEndpointBuilder
import springdox.documentation.builders.TokenRequestEndpointBuilder
import springdox.documentation.service.ApiKey
import springdox.documentation.service.Authorization
import springdox.documentation.service.AuthorizationScope
import springdox.documentation.service.AuthorizationType
import springdox.documentation.service.BasicAuth
import springdox.documentation.service.GrantType
import springdox.documentation.service.OAuth
import springdox.documentation.swagger.dto.AuthorizationCodeGrant
import springdox.documentation.swagger.dto.ImplicitGrant
import springdox.documentation.swagger.dto.LoginEndpoint
import springdox.documentation.swagger.dto.TokenEndpoint
import springdox.documentation.swagger.dto.TokenRequestEndpoint
import springdox.documentation.swagger.mixins.MapperSupport

import static com.google.common.collect.Lists.*

@Mixin(MapperSupport)
class AuthorizationTypesMapperSpec extends Specification {
  AuthorizationTypesMapper sut = authMapper()

  def "OAuth gets mapped correctly"() {
    given:
      OAuth built = createOAuth()
    when:
      def mapped = sut.toSwaggerOAuth(built)
    and:
      ImplicitGrant mappedImplicitGrant =
              new ImplicitGrant(new LoginEndpoint("uri:login"), "oauth-implicit")
      AuthorizationCodeGrant mappedAuthCodeGrant = new AuthorizationCodeGrant(
              new TokenRequestEndpoint("uri:tokenReqEndpoint", "oauthSpec", "superSecret"),
              new TokenEndpoint("uri:tokenEndpoint", "oauth-test-token"))
    then:
      mapped.type == built.type
      mapped.grantTypes.size() == 2
      mapped.scopes.size() == 1
      ImplicitGrant actualImplicitGrant = mapped.grantTypes.find { it instanceof ImplicitGrant }
      actualImplicitGrant.type == mappedImplicitGrant.type
      actualImplicitGrant.loginEndpoint?.url == mappedImplicitGrant.loginEndpoint.url
      actualImplicitGrant.tokenName == mappedImplicitGrant.tokenName


      AuthorizationCodeGrant actualAuthCodeGrant = mapped.grantTypes.find { it instanceof AuthorizationCodeGrant }
      actualAuthCodeGrant.tokenEndpoint?.tokenName == mappedAuthCodeGrant.tokenEndpoint.tokenName
      actualAuthCodeGrant.tokenEndpoint?.url == mappedAuthCodeGrant.tokenEndpoint.url
      actualAuthCodeGrant.tokenRequestEndpoint.clientIdName == mappedAuthCodeGrant.tokenRequestEndpoint.clientIdName
      actualAuthCodeGrant.tokenRequestEndpoint.clientSecretName == mappedAuthCodeGrant.tokenRequestEndpoint.clientSecretName
      actualAuthCodeGrant.tokenRequestEndpoint.url == mappedAuthCodeGrant.tokenRequestEndpoint.url

      mapped.scopes.first().scope == "oauth-spec"
      mapped.scopes.first().description == "test scope"
  }

  OAuth createOAuth() {
    springdox.documentation.service.LoginEndpoint loginEndpoint = new LoginEndpointBuilder().url("uri:login").build()
    def implicitGrant = new ImplicitGrantBuilder()
            .loginEndpoint(loginEndpoint)
            .tokenName("oauth-implicit")
            .build()
    def tokenEndpoint = new TokenEndpointBuilder()
            .tokenName("oauth-test-token")
            .url("uri:tokenEndpoint")
            .build()
    def tokenReqEndpoint = new TokenRequestEndpointBuilder()
            .clientIdName("oauthSpec")
            .clientSecretName("superSecret")
            .url("uri:tokenReqEndpoint")
            .build()
    def authCodeGrant = new  AuthorizationCodeGrantBuilder()
            .tokenEndpoint(tokenEndpoint)
            .tokenRequestEndpoint(tokenReqEndpoint)
            .build()
    def authScope = new AuthorizationScopeBuilder()
            .description("test scope")
            .scope("oauth-spec")
            .build()
    new OAuthBuilder()
            .grantTypes([implicitGrant, authCodeGrant])
            .scopes([authScope])
            .build()
  }

  def "BasicAuth gets mapped correctly"() {
    given:
      BasicAuth built = new BasicAuth()
    when:
      def mapped = sut.toSwaggerBasicAuth(built)
    then:
      built.type == mapped.type
  }

  def "ApiKey gets mapped correctly"() {
    given:
      ApiKey built = new ApiKey("api-key", "key", "header",)
    when:
      def mapped = sut.toSwaggerApiKey(built)
    then:
      built.type == mapped.type
      built.keyname == mapped.keyname
      built.passAs == mapped.passAs
  }

  def "Authorization gets mapped correctly"() {
    given:
      AuthorizationScope [] authScopes = [new AuthorizationScopeBuilder()
              .description("test scope")
              .scope("oauth-spec")
              .build()] as AuthorizationScope []
      Authorization built = new AuthorizationBuilder()
                              .type("oauth")
                              .scopes(authScopes)
                              .build()

    when:
      springdox.documentation.swagger.dto.Authorization mapped = sut.toSwaggerAuthorization(built)
    then:
      mapped.type == built.type
      mapped.scopes.size() == 1
      mapped.scopes.first().description == authScopes[0].description
      mapped.scopes.first().scope == authScopes[0].scope

      mapped.type == built.type
  }

  def "Polymorphic authorization types are handled"() {
    given:
      List<AuthorizationType> listAuthType =
              newArrayList(createOAuth(), new BasicAuth("basic"), new ApiKey("api-key", "test", "header",))

    when:
      List<springdox.documentation.swagger.dto.AuthorizationType> mapped = sut.toSwaggerAuthorizationTypes(listAuthType)
    then:
      mapped.size() == 3
  }

  def "AuthorizationTypesMapper handles unmapped grant type"() {
    given:
      AuthorizationTypesMapper mapper = authMapper()
    when:
      mapper.toSwaggerGrantType(new GrantType("unknown") {})
    then:
      thrown(UnsupportedOperationException)
  }

  def "AuthorizationTypesMapper handles list mapping with null values"() {
    given:
      AuthorizationTypesMapper mapper = authMapper()
    when:
      def mapped = mapper."toSwagger${typesToTest.simpleName}s"(null)
    then:
      mapped == null
    where:
      typesToTest << [AuthorizationScope, springdox.documentation.swagger.dto.AuthorizationType, GrantType]
  }

  def "AuthorizationTypesMapper handles unmapped authorization type"() {
    given:
      AuthorizationTypesMapper mapper = authMapper()
    when:
      mapper.toSwaggerAuthorizationType(new AuthorizationType("auth",
              "unknown") {
        @Override
        String getName() {
          throw new UnsupportedOperationException()
        }
      })
    then:
      thrown(UnsupportedOperationException)
  }
}
