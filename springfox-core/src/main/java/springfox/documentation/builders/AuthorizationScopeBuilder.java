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

import springfox.documentation.service.AuthorizationScope;

public class AuthorizationScopeBuilder {
  private String scope;
  private String description;

  /**
   * Updates the scope
   *
   * @param scope - scope for the authorization
   * @return this
   */
  public AuthorizationScopeBuilder scope(String scope) {
    this.scope = BuilderDefaults.defaultIfAbsent(scope, this.scope);
    return this;
  }

  /**
   * Updates the description of the scope
   *
   * @param description - describes what this scope represents
   * @return this
   */
  public AuthorizationScopeBuilder description(String description) {
    this.description = BuilderDefaults.defaultIfAbsent(description, this.description);
    return this;
  }

  public AuthorizationScope build() {
    return new AuthorizationScope(scope, description);
  }
}