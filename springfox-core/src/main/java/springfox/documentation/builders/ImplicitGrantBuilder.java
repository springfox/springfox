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

import springfox.documentation.service.ImplicitGrant;
import springfox.documentation.service.LoginEndpoint;

import static springfox.documentation.builders.BuilderDefaults.*;

public class ImplicitGrantBuilder {
  private LoginEndpoint loginEndpoint;
  private String tokenName;

  /**
   * Updates the login endpoint
   *
   * @param loginEndpoint - Contains the login endpoint url
   * @return this
   */
  public ImplicitGrantBuilder loginEndpoint(LoginEndpoint loginEndpoint) {
    this.loginEndpoint = defaultIfAbsent(loginEndpoint, this.loginEndpoint);
    return this;
  }

  /**
   * Updates the token name
   *
   * @param tokenName - token name
   * @return this
   */
  public ImplicitGrantBuilder tokenName(String tokenName) {
    this.tokenName = defaultIfAbsent(tokenName, this.tokenName);
    return this;
  }

  public ImplicitGrant build() {
    return new ImplicitGrant(loginEndpoint, tokenName);
  }
}