/*
 *
 *  Copyright 2015-2018 the original author or authors.
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
package springfox.documentation.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class OAuth extends SecurityScheme {

  private final List<AuthorizationScope> scopes;
  private final LinkedHashMap<String, GrantType> grantTypes;

  public OAuth(
      String name,
      List<AuthorizationScope> scopes,
      List<GrantType> grantTypes,
      List<VendorExtension> vendorExtensions) {
    super(name, "oauth2");
    this.scopes = scopes;
    this.grantTypes = initializeGrantTypes(grantTypes);
    addValidVendorExtensions(vendorExtensions);
  }

  public OAuth(
      String name,
      List<AuthorizationScope> scopes,
      List<GrantType> grantTypes) {
    this(name, scopes, grantTypes, new ArrayList<>());
  }

  private LinkedHashMap<String, GrantType> initializeGrantTypes(List<GrantType> gTypes) {
    if (null != gTypes) {
      LinkedHashMap<String, GrantType> map = new LinkedHashMap<>();
      for (GrantType grantType : gTypes) {
        map.put(grantType.getType(), grantType);
      }
      return map;
    }
    return null;
  }

  public List<AuthorizationScope> getScopes() {
    return scopes;
  }

  public List<GrantType> getGrantTypes() {
    return new ArrayList<>(grantTypes.values());
  }
}
