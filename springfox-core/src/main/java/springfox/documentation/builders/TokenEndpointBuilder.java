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

import springfox.documentation.service.TokenEndpoint;

public class TokenEndpointBuilder {
  private String url;
  private String tokenName;

  /**
   * Updates the token endpoint url
   *
   * @param url - url
   * @return this
   */
  public TokenEndpointBuilder url(String url) {
    this.url = BuilderDefaults.defaultIfAbsent(url, this.url);
    return this;
  }

  /**
   * Updates the token name
   *
   * @param tokenName - token name
   * @return this
   */
  public TokenEndpointBuilder tokenName(String tokenName) {
    this.tokenName = BuilderDefaults.defaultIfAbsent(tokenName, this.tokenName);
    return this;
  }

  public TokenEndpoint build() {
    return new TokenEndpoint(url, tokenName);
  }
}