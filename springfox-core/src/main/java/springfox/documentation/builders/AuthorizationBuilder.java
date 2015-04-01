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

import springfox.documentation.service.Authorization;
import springfox.documentation.service.AuthorizationScope;

import static springfox.documentation.builders.BuilderDefaults.*;

public class AuthorizationBuilder {
  private String type;
  private AuthorizationScope[] scopes;

  /**
   * Updates the authorization type for the api.
   *
   * @param type - Valid values for the authorization types are. rename this to reference TODO!!
   * @return this
   */
  public AuthorizationBuilder type(String type) {
    this.type = defaultIfAbsent(type, this.type);
    return this;
  }

  /**
   * Updates the authorization scopes that this authorization applies to.
   *
   * @param scopes - Scopes that this authorization applies to.
   * @return this
   */
  public AuthorizationBuilder scopes(AuthorizationScope[] scopes) {
    this.scopes = defaultIfAbsent(scopes, this.scopes);
    return this;
  }

  public Authorization build() {
    return new Authorization(type, scopes);
  }
}