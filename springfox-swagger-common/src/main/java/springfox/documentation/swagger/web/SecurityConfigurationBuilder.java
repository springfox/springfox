/*
 *
 *  Copyright 2018 the original author or authors.
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

import java.util.Map;

import static springfox.documentation.builders.BuilderDefaults.*;

public class SecurityConfigurationBuilder {

  /*--------------------------------------------*\
   * OAuth2
  \*--------------------------------------------*/
  private String clientId;
  private String clientSecret;
  private String realm;
  private String appName;
  private String scopeSeparator;
  private Map<String, Object> additionalQueryStringParams;
  private Boolean useBasicAuthenticationWithAccessCodeGrant;
  private Boolean enableCsrfSupport;

  private SecurityConfigurationBuilder() {
  }

  public static SecurityConfigurationBuilder builder() {
    return new SecurityConfigurationBuilder();
  }

  public SecurityConfiguration build() {
    return new SecurityConfiguration(
        defaultIfAbsent(
            clientId,
            null),
        defaultIfAbsent(
            clientSecret,
            null),
        defaultIfAbsent(
            realm,
            null),
        defaultIfAbsent(
            appName,
            null),
        defaultIfAbsent(
            scopeSeparator,
            null),
        defaultIfAbsent(
            additionalQueryStringParams,
            null),
        defaultIfAbsent(
            useBasicAuthenticationWithAccessCodeGrant,
            null),
        defaultIfAbsent(
            enableCsrfSupport,
            null)
    );
  }

  /**
   * @param clientId Default clientId.
   * @return this
   */
  public SecurityConfigurationBuilder clientId(String clientId) {
    this.clientId = clientId;
    return this;
  }

  /**
   * @param clientSecret Default clientSecret.
   * @return this
   */
  public SecurityConfigurationBuilder clientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
    return this;
  }

  /**
   * @param realm Realm query parameter (for oauth1) added to authorizationUrl and tokenUrl.
   * @return this
   */
  public SecurityConfigurationBuilder realm(String realm) {
    this.realm = realm;
    return this;
  }

  /**
   * @param appName Application name, displayed in authorization popup.
   * @return this
   */
  public SecurityConfigurationBuilder appName(String appName) {
    this.appName = appName;
    return this;
  }

  /**
   * @param scopeSeparator Scope separator for passing scopes, encoded before calling, default value is a space (encoded
   *                       value %20).
   * @return this
   */
  public SecurityConfigurationBuilder scopeSeparator(String scopeSeparator) {
    this.scopeSeparator = scopeSeparator;
    return this;
  }

  /**
   * @param additionalQueryStringParams Additional query parameters added to authorizationUrl and tokenUrl.
   * @return this
   */
  public SecurityConfigurationBuilder additionalQueryStringParams(Map<String, Object> additionalQueryStringParams) {
    this.additionalQueryStringParams = additionalQueryStringParams;
    return this;
  }

  /**
   * @param useBasicAuthenticationWithAccessCodeGrant Only activated for the accessCode flow. During the
   *                                                  authorization_code request to the tokenUrl, pass the Client
   *                                                  Password using the HTTP Basic Authentication scheme (Authorization
   *                                                  header with Basic base64encoded[client_id:client_secret]). The
   *                                                  default is false.
   * @return this
   */
  public SecurityConfigurationBuilder useBasicAuthenticationWithAccessCodeGrant(
      Boolean useBasicAuthenticationWithAccessCodeGrant) {
    this.useBasicAuthenticationWithAccessCodeGrant = useBasicAuthenticationWithAccessCodeGrant;
    return this;
  }

  /**
   * @param enableCsrfSupport Try to find csrf token and add it to the header of all requests
   *                          by patching the requestInterceptor.
   * @return this
   */
  public SecurityConfigurationBuilder enableCsrfSupport(Boolean enableCsrfSupport) {
    this.enableCsrfSupport = enableCsrfSupport;
    return this;
  }
}
