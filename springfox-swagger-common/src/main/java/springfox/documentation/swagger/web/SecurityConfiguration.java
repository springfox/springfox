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

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecurityConfiguration {
  static final SecurityConfiguration DEFAULT = new SecurityConfiguration();

  private final String clientId;
  private final String clientSecret;
  private final String realm;
  private final String appName;
  private final String apiKey;
  private final ApiKeyVehicle apiKeyVehicle;
  private final String scopeSeparator;
  private final String apiKeyName;

  private SecurityConfiguration() {
    this(null, null, null, null, null, ApiKeyVehicle.HEADER, "api_key", ",");
  }

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
  }

  @JsonProperty("clientId")
  public String getClientId() {
    return clientId;
  }

  @JsonProperty("realm")
  public String getRealm() {
    return realm;
  }

  @JsonProperty("appName")
  public String getAppName() {
    return appName;
  }

  @JsonProperty("apiKey")
  public String getApiKey() {
    return apiKey;
  }

  @JsonProperty("apiKeyName")
  public String getApiKeyName() {
    return apiKeyName;
  }

  @JsonProperty("clientSecret")
  public String getClientSecret() {
    return clientSecret;
  }

  @JsonProperty("scopeSeparator")
  public String scopeSeparator() {
    return scopeSeparator;
  }

  @JsonProperty("apiKeyVehicle")
  public String getApiKeyVehicle() {
    return apiKeyVehicle.getValue();
  }
}
