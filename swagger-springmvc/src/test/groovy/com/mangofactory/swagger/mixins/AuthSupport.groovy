package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.models.servicemodel.Authorization
import com.mangofactory.swagger.models.servicemodel.builder.AuthorizationBuilder
import com.mangofactory.swagger.models.servicemodel.AuthorizationCodeGrant
import com.mangofactory.swagger.models.servicemodel.AuthorizationScope
import com.mangofactory.swagger.models.servicemodel.AuthorizationType
import com.mangofactory.swagger.models.servicemodel.GrantType
import com.mangofactory.swagger.models.servicemodel.ImplicitGrant
import com.mangofactory.swagger.models.servicemodel.LoginEndpoint
import com.mangofactory.swagger.models.servicemodel.OAuth
import com.mangofactory.swagger.models.servicemodel.builder.OAuthBuilder
import com.mangofactory.swagger.models.servicemodel.TokenEndpoint
import com.mangofactory.swagger.models.servicemodel.TokenRequestEndpoint
import com.mangofactory.swagger.models.servicemodel.builder.AuthorizationCodeGrantBuilder

import static com.google.common.collect.Lists.*

class AuthSupport {
  def defaultAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything")
    AuthorizationScope[] authorizationScopes = [authorizationScope] as AuthorizationScope[];
    newArrayList(new AuthorizationBuilder().type("oauth2").scopes(authorizationScopes).build())
  }

  def oAuth() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything")
    List<AuthorizationScope> authorizationScopes = newArrayList(authorizationScope)
    GrantType grantType = new AuthorizationCodeGrant(new TokenRequestEndpoint("some:auth:uri", "test", "secret"),
            new TokenEndpoint("some:uri", "XX-TOKEN"))
    List<GrantType> grantTypes = newArrayList(grantType)
    OAuth oAuth = new OAuthBuilder().scopes(authorizationScopes).grantTypes(grantTypes).build()
    List<Authorization> authorizations = [oAuth];
    authorizations
  }

  def authorizationTypes() {
    def authorizationTypes = new ArrayList<AuthorizationType>()

    List<AuthorizationScope> authorizationScopeList = newArrayList();
    authorizationScopeList.add(new AuthorizationScope("global", "access all"));


    List<GrantType> grantTypes = newArrayList();

    LoginEndpoint loginEndpoint = new LoginEndpoint("http://petstore.swagger.wordnik.com/oauth/dialog");
    grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));


    TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("http://petstore.swagger.wordnik.com/oauth/requestToken", "client_id", "client_secret")
    TokenEndpoint tokenEndpoint = new TokenEndpoint("http://petstore.swagger.wordnik.com/oauth/token", "auth_code")

    AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrantBuilder()
            .tokenRequestEndpoint(tokenRequestEndpoint)
            .tokenEndpoint(tokenEndpoint)
            .build()

    grantTypes.add(authorizationCodeGrant)

    OAuth oAuth = new OAuthBuilder()
            .scopes(authorizationScopeList)
            .grantTypes(grantTypes)
            .build();
    return oAuth
  }

  def assertDefaultAuth(json) {
    def oauth2 = json.authorizations.get('oauth2')

    assert oauth2.type == "oauth2"
    assert oauth2.scopes[0].scope == "global"
    assert oauth2.scopes[0].description == "access all"

    def implicit = oauth2.grantTypes.implicit
    assert implicit.loginEndpoint.url == "http://petstore.swagger.wordnik.com/oauth/dialog"
    assert implicit.tokenName == "access_token"

    def tokenRequestEndpoint = oauth2.grantTypes.authorization_code.tokenRequestEndpoint
    assert tokenRequestEndpoint.url == 'http://petstore.swagger.wordnik.com/oauth/requestToken'
    assert tokenRequestEndpoint.clientIdName == 'client_id'
    assert tokenRequestEndpoint.clientSecretName == 'client_secret'

    def tokenEndpoint = oauth2.grantTypes.authorization_code.tokenEndpoint
    assert tokenEndpoint.url == 'http://petstore.swagger.wordnik.com/oauth/token'
    assert tokenEndpoint.tokenName == 'auth_code'
    true
  }
}
