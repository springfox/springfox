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

import java.util.List;

public class ResourceListing {
  private final String apiVersion;
  private final List<ApiListingReference> apis;
  private final List<SecurityScheme> securitySchemes;
  private final ApiInfo info;

  public ResourceListing(String apiVersion,
                         List<ApiListingReference> apis,
                         List<SecurityScheme> securitySchemes,
                         ApiInfo info) {

    this.apiVersion = apiVersion;
    this.apis = apis;
    this.securitySchemes = securitySchemes;
    this.info = info;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public List<ApiListingReference> getApis() {
    return apis;
  }

  public List<SecurityScheme> getSecuritySchemes() {
    return securitySchemes;
  }

  public ApiInfo getInfo() {
    return info;
  }
}
