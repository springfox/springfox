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

import springfox.documentation.schema.Model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiListing {
  private final String apiVersion;
  private final String basePath;
  private final String resourcePath;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final Set<String> protocol;
  private final List<SecurityReference> securityReferences;
  private final List<ApiDescription> apis;
  private final Map<String, Model> models;
  private final String description;
  private final int position;

  public ApiListing(String apiVersion, String basePath, String resourcePath, Set<String>
          produces, Set<String> consumes, Set<String> protocol, List<SecurityReference> securityReferences,
                    List<ApiDescription> apis, Map<String, Model> models, String description, int position) {
    this.apiVersion = apiVersion;
    this.basePath = basePath;
    this.resourcePath = resourcePath;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.securityReferences = securityReferences;
    this.apis = apis;
    this.models = models;
    this.description = description;
    this.position = position;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public String getBasePath() {
    return basePath;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public Set<String> getProduces() {
    return produces;
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public Set<String> getProtocols() {
    return protocol;
  }

  public List<SecurityReference> getSecurityReferences() {
    return securityReferences;
  }

  public List<ApiDescription> getApis() {
    return apis;
  }

  public Map<String, Model> getModels() {
    return models;
  }

  public String getDescription() {
    return description;
  }

  public int getPosition() {
    return position;
  }
}

