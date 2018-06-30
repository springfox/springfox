/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger1.dto



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
            "url" : "http://petstore.swagger.io/oauth/dialog"
          },
          "tokenName" : "access_token",
          "type" : "implicit"
        },
        "authorization_code" : {
          "tokenEndpoint" : {
            "tokenName" : "auth_code",
            "url" : "http://petstore.swagger.io/oauth/token"
          },
          "tokenRequestEndpoint" : {
            "clientIdName" : "client_id",
            "clientSecretName" : "client_secret",
            "url" : "http://petstore.swagger.io/oauth/requestToken"
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
      writePretty(
              new ResourceListing("apiVersion"
                      , "swagger version"
                      , [apiListingReference()]
                      , null
                      , apiInfo())
      ) == """{
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
    List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
    authorizationScopeList.add(new AuthorizationScope("global", "access all"));

    List<GrantType> grantTypes = new ArrayList<>();

    LoginEndpoint loginEndpoint = new LoginEndpoint("http://petstore.swagger.io/oauth/dialog")
    grantTypes.add(new ImplicitGrant(loginEndpoint, "access_token"))

    TokenRequestEndpoint tokenRequestEndpoint = new TokenRequestEndpoint( "http://petstore.swagger.io/oauth/requestToken"
            , "client_id"
            , "client_secret")

    TokenEndpoint tokenEndpoint = new TokenEndpoint("http://petstore.swagger.io/oauth/token", "auth_code")
    
    AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(tokenRequestEndpoint, tokenEndpoint)

    grantTypes.add(authorizationCodeGrant);


    OAuth oAuth = new OAuth(authorizationScopeList, grantTypes)

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
