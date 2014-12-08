package com.mangofactory.swagger.models.dto

import static com.google.common.collect.Lists.newArrayList

class ResourceListingSpec extends InternalJsonSerializationSpec {

  def "should serialize"() {
    expect:
      writePretty(resourceListing()) == """{
  "apiVersion" : "apiVersion",
  "apis" : [ {
    "description" : "description",
    "path" : "/path",
    "position" : 3
  } ],
  "authorizations" : {
    "oauth2" : {
      "grantTypes" : {
        "implicit" : {
          "loginEndpoint" : {
            "url" : "http://petstore.swagger.wordnik.com/oauth/dialog"
          },
          "tokenName" : "access_token",
          "type" : "implicit"
        },
        "authorization_code" : {
          "tokenEndpoint" : {
            "tokenName" : "auth_code",
            "url" : "http://petstore.swagger.wordnik.com/oauth/token"
          },
          "tokenRequestEndpoint" : {
            "clientIdName" : "client_id",
            "clientSecretName" : "client_secret",
            "url" : "http://petstore.swagger.wordnik.com/oauth/requestToken"
          },
          "type" : "authorization_code"
        }
      },
      "scopes" : [ {
        "description" : "access all",
        "scope" : "global"
      } ],
      "type" : "oauth2"
    }
  },
  "info" : {
    "contact" : "Contact Email",
    "description" : "Api Description",
    "license" : "Licence Type",
    "licenseUrl" : "License URL",
    "termsOfServiceUrl" : "Api terms of service",
    "title" : " Title"
  },
  "swaggerVersion" : "swagger version"
}"""
  }

  def "should pass coverage"() {
    expect:
      ResourceListing api = resourceListing()
      api.apis
      api.apiVersion
      api.authorizations
      api.info
      api.swaggerVersion
  }

  def "should not initialize auth types"() {
    expect:
      writePretty(new ResourceListing(
              "apiVersion",
              "swagger version",
              [apiListingReference()],
              null,
              apiInfo())) == """{
  "apiVersion" : "apiVersion",
  "apis" : [ {
    "description" : "description",
    "path" : "/path",
    "position" : 3
  } ],
  "info" : {
    "contact" : "Contact Email",
    "description" : "Api Description",
    "license" : "Licence Type",
    "licenseUrl" : "License URL",
    "termsOfServiceUrl" : "Api terms of service",
    "title" : " Title"
  },
  "swaggerVersion" : "swagger version"
}"""

  }


  def apiListingReference() {
    new ApiListingReference("/path", "description", 3)
  }


  def resourceListing() {
    List<AuthorizationScope> authorizationScopeList = newArrayList();
    authorizationScopeList.add(new AuthorizationScope("global", "access all"));

    List<GrantType> grantTypes = newArrayList();

    LoginEndpoint loginEndpoint = new LoginEndpoint("http://petstore.swagger.wordnik.com/oauth/dialog");
    grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"));

    TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint("http://petstore.swagger.wordnik.com/oauth/requestToken", "client_id", "client_secret");
    TokenEndpoint tokenEndpoint = new TokenEndpoint("http://petstore.swagger.wordnik.com/oauth/token", "auth_code");

    AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint);
    grantTypes.add(authorizationCodeGrant);


    OAuth oAuth = new OAuthBuilder()
            .scopes(authorizationScopeList)
            .grantTypes(grantTypes)
            .build();

    ApiInfo apiInfo = apiInfo()

    new ResourceListing(
            "apiVersion",
            "swagger version",
            [apiListingReference()],
            [oAuth],
            apiInfo)
  }

  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo(
            " Title",
            "Api Description",
            "Api terms of service",
            "Contact Email",
            "Licence Type",
            "License URL"
    )
    apiInfo
  }
}
