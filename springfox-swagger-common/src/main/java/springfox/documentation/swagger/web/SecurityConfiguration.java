/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityConfiguration {

  /*--------------------------------------------*\
   * OAuth2
  \*--------------------------------------------*/
  private final String clientId;
  private final String clientSecret;
  private final String realm;
  private final String appName;
  private final String scopeSeparator;
  private final Map<String, Object> additionalQueryStringParams;
  private final Boolean useBasicAuthenticationWithAccessCodeGrant;

  /**
   * Default constructor
   *
   * @param clientId                                  Default clientId.
   * @param clientSecret                              Default clientSecret.
   * @param realm                                     Realm query parameter (for oauth1) added to authorizationUrl and
   *                                                  tokenUrl.
   * @param appName                                   Application name, displayed in authorization popup.
   * @param scopeSeparator                            Scope separator for passing scopes, encoded before calling,
   *                                                  default value is a space (encoded value %20).
   * @param additionalQueryStringParams               Additional query parameters added to authorizationUrl and
   *                                                  tokenUrl.
   * @param useBasicAuthenticationWithAccessCodeGrant Only activated for the accessCode flow. During the
   *                                                  authorization_code request to the tokenUrl, pass the Client
   *                                                  Password using the HTTP Basic Authentication scheme (Authorization
   *                                                  header with Basic base64encoded[client_id:client_secret]). The
   *                                                  default is false.
   */
  public SecurityConfiguration(
      String clientId,
      String clientSecret,
      String realm,
      String appName,
      String scopeSeparator,
      Map<String, Object> additionalQueryStringParams,
      Boolean useBasicAuthenticationWithAccessCodeGrant) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.realm = realm;
    this.appName = appName;
    this.scopeSeparator = scopeSeparator;
    this.additionalQueryStringParams = additionalQueryStringParams;
    this.useBasicAuthenticationWithAccessCodeGrant = useBasicAuthenticationWithAccessCodeGrant;
  }

  @JsonProperty("clientId")
  public String getClientId() {
    return clientId;
  }

  @JsonProperty("clientSecret")
  public String getClientSecret() {
    return clientSecret;
  }

  @JsonProperty("realm")
  public String getRealm() {
    return realm;
  }

  @JsonProperty("appName")
  public String getAppName() {
    return appName;
  }

  @JsonProperty("scopeSeparator")
  public String scopeSeparator() {
    return scopeSeparator;
  }

  @JsonProperty("additionalQueryStringParams")
  public Map<String, Object> getAdditionalQueryStringParams() {
    return additionalQueryStringParams;
  }

  @JsonProperty("useBasicAuthenticationWithAccessCodeGrant")
  public Boolean getUseBasicAuthenticationWithAccessCodeGrant() {
    return useBasicAuthenticationWithAccessCodeGrant;
  }
}
