/*
 *
 *  Copyright 2015-2019 the original author or authors.
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


import springfox.documentation.schema.ModelSpecification;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.*;

@SuppressWarnings("deprecation")
public class ApiListing {
  private final String apiVersion;
  private final String basePath;
  private final String resourcePath;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final String host;
  private final Set<String> protocols;
  private final List<SecurityReference> securityReferences;
  private final List<ApiDescription> apis;
  private final Map<String, springfox.documentation.schema.Model> models;
  private final Map<String, ModelSpecification> modelSpecifications;
  private final ModelNamesRegistry modelNamesRegistry;
  //  private Map<String, ApiResponse> responses = null;
//  private Map<String, Parameter> parameters = null;
//  private Map<String, Example> examples = null;
//  private Map<String, RequestBody> requestBodies = null;
//  private Map<String, Header> headers = null;
//  private Map<String, Link> links = null;
//  private Map<String, Callback> callbacks = null;
  private final String description;
  private final int position;
  private final Set<Tag> tags;

  @SuppressWarnings("ParameterNumber")
  public ApiListing(
      String apiVersion,
      String basePath,
      String resourcePath,
      Set<String> produces,
      Set<String> consumes,
      String host,
      Set<String> protocols,
      List<SecurityReference> securityReferences,
      List<ApiDescription> apis,
      Map<String, springfox.documentation.schema.Model> models,
      Map<String, ModelSpecification> modelSpecifications,
      ModelNamesRegistry modelNamesRegistry,
      String description,
      int position,
      Set<Tag> tags) {

    this.apiVersion = apiVersion;
    this.basePath = basePath;
    this.resourcePath = resourcePath;
    this.produces = produces;
    this.consumes = consumes;
    this.host = host;
    this.protocols = protocols;
    this.securityReferences = securityReferences;
    this.apis = apis.stream()
        .sorted(byPath()).collect(toList());
    this.models = models;
    this.modelSpecifications = modelSpecifications;
    this.modelNamesRegistry = modelNamesRegistry;
    this.description = description;
    this.position = position;
    this.tags = tags;
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

  public String getHost() {
    return host;
  }

  public Set<String> getProtocols() {
    return protocols;
  }

  public List<SecurityReference> getSecurityReferences() {
    return securityReferences;
  }

  public List<ApiDescription> getApis() {
    return apis;
  }

  public Map<String, springfox.documentation.schema.Model> getModels() {
    return models;
  }

  public String getDescription() {
    return description;
  }

  public int getPosition() {
    return position;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  private Comparator<ApiDescription> byPath() {
    return Comparator.comparing(ApiDescription::getPath);
  }

  public Map<String, ModelSpecification> getModelSpecifications() {
    return modelSpecifications;
  }

  public ModelNamesRegistry getModelNamesRegistry() {
    return modelNamesRegistry;
  }
}

