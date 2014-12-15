package com.mangofactory.swagger.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder({"apiVersion", "swaggerVersion", "basePath", "resourcePath", "produces", "consumes", "apis",
        "models"})
public class ApiListing {
  private final String apiVersion;
  private final String swaggerVersion;
  private final String basePath;
  private final String resourcePath;
  private final List<String> produces;
  private final List<String> consumes;
  @JsonIgnore
  private final List<String> protocol;
  @JsonInclude(Include.NON_EMPTY)
  private final List<Authorization> authorizations;
  private final List<ApiDescription> apis;
  @JsonInclude(Include.NON_EMPTY)
  private final Map<String, Model> models;
  @JsonIgnore
  private final String description;
  @JsonIgnore
  private final int position;

  public ApiListing(String apiVersion, String swaggerVersion, String basePath, String resourcePath, List<String>
          produces, List<String> consumes, List<String> protocol, List<Authorization> authorizations,
                    List<ApiDescription> apis, Map<String, Model> models, String description, int position) {
    this.apiVersion = apiVersion;
    this.swaggerVersion = swaggerVersion;
    this.basePath = basePath;
    this.resourcePath = resourcePath;
    this.produces = produces;
    this.consumes = consumes;
    this.protocol = protocol;
    this.authorizations = authorizations;
    this.apis = apis;
    this.models = models;
    this.description = description;
    this.position = position;
  }

  public String getApiVersion() {
    return apiVersion;
  }

  public String getSwaggerVersion() {
    return swaggerVersion;
  }

  public String getBasePath() {
    return basePath;
  }

  public String getResourcePath() {
    return resourcePath;
  }

  public List<String> getProduces() {
    return produces;
  }

  public List<String> getConsumes() {
    return consumes;
  }

  public List<String> getProtocol() {
    return protocol;
  }

  public List<Authorization> getAuthorizations() {
    return authorizations;
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

