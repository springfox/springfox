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
import springfox.documentation.service.GrantType;
import springfox.documentation.service.OAuth;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.BuilderDefaults.*;

public class OAuthBuilder {

  private List<AuthorizationScope> scopes = new ArrayList<AuthorizationScope>();
  private List<GrantType> grantTypes = new ArrayList<GrantType>();
  private String name;


  /**
   * Updates the authorization scopes with the new scopes
   *
   * @param scopes - represents the oauth scopes
   * @return this
   */
  public OAuthBuilder scopes(List<AuthorizationScope> scopes) {
    this.scopes.addAll(nullToEmptyList(scopes));
    return this;
  }

  /**
   * Updates the grant types that this security definition represents
   *
   * @param grantTypes - grant types
   * @return this
   */
  public OAuthBuilder grantTypes(List<GrantType> grantTypes) {
    this.grantTypes.addAll(nullToEmptyList(grantTypes));
    return this;
  }

  /**
   * Updates the unique name to identify the security definition
   *
   * @param name - name
   * @return this
   */
  public OAuthBuilder name(String name) {
    this.name = defaultIfAbsent(name, this.name);
    return this;
  }

  public OAuth build() {
    return new OAuth(name, scopes, grantTypes);
  }
}
