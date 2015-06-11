/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.builders;

import springfox.documentation.service.TokenRequestEndpoint;

public class TokenRequestEndpointBuilder {
  private String url;
  private String clientIdName;
  private String clientSecretName;

  /**
   * Updates the url for the token request endpoint
   *
   * @param url - url
   * @return this
   */
  public TokenRequestEndpointBuilder url(String url) {
    this.url = BuilderDefaults.defaultIfAbsent(url, this.url);
    return this;
  }

  /**
   * Updates the client id name
   *
   * @param clientIdName - client id
   * @return this
   */
  public TokenRequestEndpointBuilder clientIdName(String clientIdName) {
    this.clientIdName = BuilderDefaults.defaultIfAbsent(clientIdName, this.clientIdName);
    return this;
  }

  /**
   * Updates the client secret name
   *
   * @param clientSecretName - client secret name
   * @return this
   */
  public TokenRequestEndpointBuilder clientSecretName(String clientSecretName) {
    this.clientSecretName = BuilderDefaults.defaultIfAbsent(clientSecretName, this.clientSecretName);
    return this;
  }

  public TokenRequestEndpoint build() {
    return new TokenRequestEndpoint(url, clientIdName, clientSecretName);
  }
}