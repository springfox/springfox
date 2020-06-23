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

package springfox.documentation.swagger1.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@JsonPropertyOrder({ "apiVersion", "swaggerVersion", "basePath", "resourcePath", "produces", "consumes", "apis",
    "models" })
public class ApiListing {
  private String apiVersion;
  private String swaggerVersion;
  private String basePath;
  private String resourcePath;
  private Set<String> produces;
  private Set<String> consumes;
  private List<ApiDescription> apis;

  @JsonIgnore
  private Set<String> protocols;
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

  public Set<String> getProduces() {
    return produces;
  }

  public void setProduces(Set<String> produces) {
    if (produces != null) {
      if (this.produces == null) {
        this.produces = new TreeSet<>();
      }
      this.produces.addAll(produces);
    }
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public void setConsumes(Set<String> consumes) {
    if (consumes != null) {
      if (this.consumes == null) {
        this.consumes = new TreeSet<>();
      }
      this.consumes.addAll(consumes);
    }
  }

  public Set<String> getProtocols() {
    return protocols;
  }

  public void setProtocols(Set<String> protocols) {
    if (protocols != null) {
      if (this.protocols == null) {
        this.protocols = new TreeSet<>();
      }
      this.protocols.addAll(protocols);
    }
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
    this.apis = apis.stream()
        .sorted(byPath()
            .thenComparing(byDescription())).collect(toList());
  }

  public Map<String, ModelDto> getModels() {
    return models;
  }

  public void setModels(Map<String, ModelDto> models) {
    if (models != null) {
      if (this.models == null) {
        this.models = new TreeMap<>();
      }
      this.models.putAll(models);
    }
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

  public void appendAuthorizations(List<Authorization> authorizations) {
    if (!nullToEmptyList(authorizations).isEmpty()) {
      this.authorizations = nullToEmptyList(this.authorizations);
      this.authorizations.addAll(authorizations);
    }
  }

  public void appendApis(List<ApiDescription> apis) {
    if (!nullToEmptyList(apis).isEmpty()) {
      this.apis = nullToEmptyList(this.apis);
      this.apis.addAll(apis);
    }
  }

  public void appendProtocols(Set<String> protocols) {
    if (!nullToEmptySet(protocols).isEmpty()) {
      this.protocols = nullToEmptySet(this.protocols);
      this.protocols.addAll(protocols);
    }
  }

  public void appendConsumes(Set<String> consumes) {
    if (!nullToEmptySet(consumes).isEmpty()) {
      this.consumes = nullToEmptySet(this.consumes);
      this.consumes.addAll(consumes);
    }
  }

  public void appendProduces(Set<String> produces) {
    if (!nullToEmptySet(produces).isEmpty()) {
      this.produces = nullToEmptySet(this.produces);
      this.produces.addAll(produces);
    }
  }

  public void appendModels(Map<String, ModelDto> models) {
    if (!nullToEmptyMap(models).isEmpty()) {
      this.models = nullToEmptyMap(this.models);
      this.models.putAll(models);
    }
  }

  private Comparator<ApiDescription> byPath() {
    return Comparator.comparing(ApiDescription::getPath);
  }

  private Comparator<ApiDescription> byDescription() {
    return Comparator.comparing(ApiDescription::getDescription);
  }
}

