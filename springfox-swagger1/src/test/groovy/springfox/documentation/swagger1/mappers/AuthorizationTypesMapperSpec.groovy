/*
 *
 *  Copyright 2017-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.mappers

import spock.lang.Specification
import springfox.documentation.builders.AuthorizationCodeGrantBuilder
import springfox.documentation.builders.AuthorizationScopeBuilder
import springfox.documentation.builders.ImplicitGrantBuilder
import springfox.documentation.builders.LoginEndpointBuilder
import springfox.documentation.builders.OAuthBuilder
import springfox.documentation.builders.TokenRequestEndpointBuilder
import springfox.documentation.service.ApiKey
import springfox.documentation.service.AuthorizationScope
import springfox.documentation.service.BasicAuth
import springfox.documentation.service.GrantType
import springfox.documentation.service.OAuth
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.SecurityScheme
import springfox.documentation.swagger1.dto.Authorization
import springfox.documentation.swagger1.dto.AuthorizationCodeGrant
import springfox.documentation.swagger1.dto.ImplicitGrant
import springfox.documentation.swagger1.dto.LoginEndpoint
import springfox.documentation.swagger1.dto.TokenEndpoint
import springfox.documentation.swagger1.dto.TokenRequestEndpoint
import springfox.documentation.swagger1.mixins.MapperSupport

import java.util.stream.Stream

import static java.util.stream.Collectors.*

class AuthorizationTypesMapperSpec extends Specification implements MapperSupport {
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
    springfox.documentation.service.LoginEndpoint loginEndpoint = new LoginEndpointBuilder().url("uri:login").build()
    def implicitGrant = new ImplicitGrantBuilder()
        .loginEndpoint(loginEndpoint)
        .tokenName("oauth-implicit")
        .build()
    def authCodeGrant = new AuthorizationCodeGrantBuilder()
        .tokenEndpoint {
          it.tokenName("oauth-test-token")
              .url("uri:tokenEndpoint")
        }
        .tokenRequestEndpoint {
          it.clientIdName("oauthSpec")
              .clientSecretName("superSecret")
              .url("uri:tokenReqEndpoint")
        }
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
    BasicAuth built = new BasicAuth("auth")
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
    AuthorizationScope[] authScopes = [new AuthorizationScopeBuilder()
                                           .description("test scope")
                                           .scope("oauth-spec")
                                           .build()] as AuthorizationScope[]
    SecurityReference built = new SecurityReference.SecurityReferenceBuilder()
        .reference("oauth")
        .scopes(authScopes)
        .build()

    when:
    Authorization mapped = sut.toSwaggerSecurityReference(built)
    then:
    mapped.type == built.reference
    mapped.scopes.size() == 1
    mapped.scopes.first().description == authScopes[0].description
    mapped.scopes.first().scope == authScopes[0].scope

    mapped.type == built.reference
  }

  def "Polymorphic authorization types are handled"() {
    given:
    List<SecurityScheme> listAuthType =
        Stream.of(createOAuth(), new BasicAuth("basic"), new ApiKey("api-key", "test", "header",)).collect(toList())

    when:
    List<springfox.documentation.swagger1.dto.AuthorizationType> mapped = sut.toSwaggerAuthorizationTypes(listAuthType)
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
    typesToTest << [AuthorizationScope, springfox.documentation.swagger1.dto.AuthorizationType, GrantType]
  }

  def "AuthorizationTypesMapper handles unmapped authorization type"() {
    given:
    AuthorizationTypesMapper mapper = authMapper()
    when:
    mapper.toSwaggerAuthorizationType(new SecurityScheme("auth",
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
