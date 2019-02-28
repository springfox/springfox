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

package springfox.documentation.swagger1.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class OAuth extends AuthorizationType {

  private List<AuthorizationScope> scopes;
  private LinkedHashMap<String, GrantType> grantTypes;

  public OAuth() {
    super("oauth2");
  }

  public OAuth(List<AuthorizationScope> scopes, List<GrantType> gTypes) {
    super("oauth2");
    this.scopes = scopes;
    this.grantTypes = initializeGrantTypes(gTypes);

  }

  private LinkedHashMap<String, GrantType> initializeGrantTypes(List<GrantType> gTypes) {
    if (null != gTypes) {
      LinkedHashMap<String, GrantType> map = new LinkedHashMap<String, GrantType>();
      for (GrantType grantType : gTypes) {
        map.put(grantType.getType(), grantType);
      }
      return map;
    }
    return null;
  }

  @Override
  public String getName() {
    return getType();
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public void setScopes(List<AuthorizationScope> scopes) {
    this.scopes = scopes;
  }

  public List<GrantType> getGrantTypes() {
    return new ArrayList<GrantType>(grantTypes.values());
  }

  public void setGrantTypes(List<GrantType> grantTypes) {
    this.grantTypes = initializeGrantTypes(grantTypes);
  }
}
