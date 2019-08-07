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
package springfox.documentation.swagger.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityConfiguration {
  /**
   * @deprecated @since 2.8.0. Use the {@link SecurityConfigurationBuilder} instead
   */
  @Deprecated
  static final SecurityConfiguration DEFAULT = new SecurityConfiguration();

  /**
   * @deprecated @since 2.8.0. This field is unused
   */
  @Deprecated
  private String apiKey;

  /**
   * @deprecated @since 2.8.0. This field is unused
   */
  @Deprecated
  private ApiKeyVehicle apiKeyVehicle;

  /**
   * @deprecated @since 2.8.0. This field is unused
   */
  @Deprecated
  private String apiKeyName;

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
  /*--------------------------------------------*\
   * CSRF
  \*--------------------------------------------*/
  private final Boolean enableCsrfSupport;

  /**
   * @deprecated @since 2.8.0. Use the {@link SecurityConfigurationBuilder} instead
   */
  private SecurityConfiguration() {
    this(
        null,
        null,
        null,
        null,
        null,
        ApiKeyVehicle.HEADER,
        "api_key",
        ",");
  }

  /**
   * @param clientId       - client id
   * @param clientSecret   - client secret
   * @param realm          - realm
   * @param appName        - application name
   * @param apiKey         - api key
   * @param apiKeyVehicle  - how the api key is transmitted
   * @param apiKeyName     - name of the api key
   * @param scopeSeparator - scope separator
   * @deprecated @since 2.8.0. Use the {@link SecurityConfigurationBuilder} instead
   */
  @Deprecated
  @SuppressWarnings("ParameterNumber")
  public SecurityConfiguration(
      String clientId,
      String clientSecret,
      String realm,
      String appName,
      String apiKey,
      ApiKeyVehicle apiKeyVehicle,
      String apiKeyName,
      String scopeSeparator) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.realm = realm;
    this.appName = appName;
    this.apiKey = apiKey;
    this.apiKeyVehicle = apiKeyVehicle;
    this.apiKeyName = apiKeyName;
    this.scopeSeparator = scopeSeparator;

    this.additionalQueryStringParams = null;
    this.useBasicAuthenticationWithAccessCodeGrant = null;
    this.enableCsrfSupport = null;
  }

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
   *                                                  Password using the HTTP Basic Authentication scheme
   *                                                  (Authorization header with Basic
   *                                                  base64encoded[client_id:client_secret]). The default is false.
   * @param enableCsrfSupport                         Enable csrf support, default is false.
   */
  @SuppressWarnings("ParameterNumber")
  public SecurityConfiguration(
      String clientId,
      String clientSecret,
      String realm,
      String appName,
      String scopeSeparator,
      Map<String, Object> additionalQueryStringParams,
      Boolean useBasicAuthenticationWithAccessCodeGrant,
      Boolean enableCsrfSupport) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.realm = realm;
    this.appName = appName;
    this.scopeSeparator = scopeSeparator;
    this.additionalQueryStringParams = additionalQueryStringParams;
    this.useBasicAuthenticationWithAccessCodeGrant = useBasicAuthenticationWithAccessCodeGrant;
    this.enableCsrfSupport = enableCsrfSupport;
  }

  /**
   * @return apiKey
   * @deprecated @since 2.8.0
   */
  @Deprecated
  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }

  /**
   * @return apiKeyName
   * @deprecated @since 2.8.0
   */
  @Deprecated
  @JsonProperty("apiKeyName")
  public String getApiKeyName() {
    return apiKeyName;
  }

  /**
   * @return apiKeyVehicle - header, cookie etc.
   * @deprecated @since 2.8.0
   */
  @Deprecated
  @JsonProperty("apiKeyVehicle")
  public String getApiKeyVehicle() {
    if (apiKeyVehicle != null) {
      return apiKeyVehicle.getValue();
    }
    return null;
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

  @JsonProperty("enableCsrfSupport")
  public Boolean getEnableCsrfSupport() {
    return enableCsrfSupport;
  }
}
