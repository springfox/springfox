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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"apiVersion", "swaggerVersion", "basePath", "resourcePath", "produces", "consumes", "apis",
        "models"})
public class ApiListing {
  private String apiVersion;
  private String swaggerVersion;
  private String basePath;
  private String resourcePath;
  private List<String> produces;
  private List<String> consumes;
  private List<ApiDescription> apis;

  @JsonIgnore
  private List<String> protocols;
  @JsonInclude(Include.NON_EMPTY)
  private List<Authorization> authorizations;
  @JsonInclude(Include.NON_EMPTY)
  private Map<String, ModelDto> models;
  @JsonIgnore
  private String description;
  @JsonIgnore
  private int position;

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public String getSwaggerVersion() {
    return swaggerVersion;
  }

  public void setSwaggerVersion(String swaggerVersion) {
    this.swaggerVersion = swaggerVersion;
  }

  public String getBasePath() {
    return basePath;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public void setResourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public List<String> getProduces() {
    return produces;
  }

  public void setProduces(List<String> produces) {
    this.produces = produces;
  }

  public List<String> getConsumes() {
    return consumes;
  }

  public void setConsumes(List<String> consumes) {
    this.consumes = consumes;
  }

  public List<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(List<String> protocols) {
    this.protocols = protocols;
  }

  public List<Authorization> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(List<Authorization> authorizations) {
    this.authorizations = authorizations;
  }

  public List<ApiDescription> getApis() {
    return apis;
  }

  public void setApis(List<ApiDescription> apis) {
    this.apis = apis;
  }

  public Map<String, ModelDto> getModels() {
    return models;
  }

  public void setModels(Map<String, ModelDto> models) {
    this.models = models;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }
}

