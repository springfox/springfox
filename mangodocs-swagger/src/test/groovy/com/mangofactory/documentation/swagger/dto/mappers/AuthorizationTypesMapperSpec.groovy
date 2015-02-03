package com.mangofactory.documentation.swagger.dto.mappers
import com.mangofactory.documentation.service.model.ApiKey
import com.mangofactory.documentation.service.model.Authorization
import com.mangofactory.documentation.service.model.AuthorizationScope
import com.mangofactory.documentation.service.model.BasicAuth
import com.mangofactory.documentation.service.model.GrantType
import com.mangofactory.documentation.service.model.LoginEndpoint
import com.mangofactory.documentation.service.model.OAuth
import com.mangofactory.documentation.builder.AuthorizationBuilder
import com.mangofactory.documentation.builder.AuthorizationCodeGrantBuilder
import com.mangofactory.documentation.builder.AuthorizationScopeBuilder
import com.mangofactory.documentation.builder.ImplicitGrantBuilder
import com.mangofactory.documentation.builder.LoginEndpointBuilder
import com.mangofactory.documentation.builder.OAuthBuilder
import com.mangofactory.documentation.builder.TokenEndpointBuilder
import com.mangofactory.documentation.builder.TokenRequestEndpointBuilder
import com.mangofactory.documentation.swagger.dto.AuthorizationCodeGrant
import com.mangofactory.documentation.swagger.dto.AuthorizationType
import com.mangofactory.documentation.swagger.dto.ImplicitGrant
import com.mangofactory.documentation.swagger.dto.TokenEndpoint
import com.mangofactory.documentation.swagger.dto.TokenRequestEndpoint
import com.mangofactory.documentation.swagger.mixins.MapperSupport
import spock.lang.Specification

import static com.google.common.collect.Lists.newArrayList

@Mixin(MapperSupport)
class AuthorizationTypesMapperSpec extends Specification {
  AuthorizationTypesMapper sut = authMapper()

  def "OAuth gets mapped correctly"() {
    given:
      OAuth built = createOAuth()
    when:
      def mapped = sut.toSwaggerOAuth(built)
    and:
      ImplicitGrant mappedImplicitGrant = new ImplicitGrant(new com.mangofactory.documentation.swagger.dto.LoginEndpoint("uri:login"),
              "oauth-implicit")
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
    LoginEndpoint loginEndpoint = new LoginEndpointBuilder().url("uri:login").build()
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
      ApiKey built = new ApiKey("key", "header")
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
      com.mangofactory.documentation.swagger.dto.Authorization mapped = sut.toSwaggerAuthorization(built)
    then:
      mapped.type == built.type
      mapped.scopes.size() == 1
      mapped.scopes.first().description == authScopes[0].description
      mapped.scopes.first().scope == authScopes[0].scope

      mapped.type == built.type
  }

  def "Polymorphic authorization types are handled"() {
    given:
      List<com.mangofactory.documentation.service.model.AuthorizationType> listAuthType =
              newArrayList(createOAuth(), new BasicAuth(), new ApiKey("test", "header"))

    when:
      List<AuthorizationType> mapped = sut.toSwaggerAuthorizationTypes(listAuthType)
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
      typesToTest << [AuthorizationScope, AuthorizationType, GrantType]
  }

  def "AuthorizationTypesMapper handles unmapped authorization type"() {
    given:
      AuthorizationTypesMapper mapper = authMapper()
    when:
      mapper.toSwaggerAuthorizationType(new com.mangofactory.documentation.service.model.AuthorizationType("unknown") {
        @Override
        String getName() {
          throw new UnsupportedOperationException()
        }
      })
    then:
      thrown(UnsupportedOperationException)
  }
}
