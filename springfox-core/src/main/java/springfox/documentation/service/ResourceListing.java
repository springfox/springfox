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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Lists.*;

public class ResourceListing {
  private final String apiVersion;
  private final List<ApiListingReference> apis;
  private final LinkedHashMap<String, SecurityScheme> securitySchemes;
  private final ApiInfo info;

  public ResourceListing(String apiVersion,
                         List<ApiListingReference> apis,
                         List<SecurityScheme> securitySchemes,
                         ApiInfo info) {

    this.apiVersion = apiVersion;
    this.apis = apis;
    this.securitySchemes = initializeSecuritySchemes(securitySchemes);
    this.info = info;
  }

  private LinkedHashMap<String, SecurityScheme> initializeSecuritySchemes(List<SecurityScheme> securitySchemes) {
    LinkedHashMap<String, SecurityScheme> mapped = new LinkedHashMap<String, SecurityScheme>();
    List<SecurityScheme> emptyList = newArrayList();
    for (SecurityScheme securityScheme : fromNullable(securitySchemes).or(emptyList)) {
      mapped.put(securityScheme.getType(), securityScheme);
    }
    return mapped;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public List<ApiListingReference> getApis() {
    return apis;
  }

  public List<SecurityScheme> getSecuritySchemes() {
    return new ArrayList<SecurityScheme>(securitySchemes.values());
  }

  public ApiInfo getInfo() {
    return info;
  }
}
