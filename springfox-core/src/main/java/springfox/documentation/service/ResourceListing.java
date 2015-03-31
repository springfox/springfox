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

package springfox.documentation.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ResourceListing {
  private final String apiVersion;
  private final List<ApiListingReference> apis;
  private final LinkedHashMap<String, AuthorizationType> authorizations;
  private final ApiInfo info;

  public ResourceListing(String apiVersion,
                         List<ApiListingReference> apis,
                         List<AuthorizationType> authorizations,
                         ApiInfo info) {

    this.apiVersion = apiVersion;
    this.apis = apis;
    this.authorizations = initializeAuthTypes(authorizations);
    this.info = info;
  }

  private LinkedHashMap<String, AuthorizationType> initializeAuthTypes(List<AuthorizationType> authorizationTypes) {
    LinkedHashMap<String, AuthorizationType> mapped = new LinkedHashMap<String, AuthorizationType>();
    List<AuthorizationType> emptyList = Lists.newArrayList();
    for (AuthorizationType authorizationType : Optional.fromNullable(authorizationTypes).or(emptyList)) {
      mapped.put(authorizationType.getType(), authorizationType);
    }
    return mapped;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public List<ApiListingReference> getApis() {
    return apis;
  }

  public List<AuthorizationType> getAuthorizations() {
    return new ArrayList<AuthorizationType>(authorizations.values());
  }

  public ApiInfo getInfo() {
    return info;
  }
}
