package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.ApiListing;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.Authorization;
import com.mangofactory.service.model.Model;

import java.util.List;
import java.util.Map;

public class ApiListingBuilder {
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private List<String> produces;
  private List<String> consumes;
  private List<String> protocol;
  private List<Authorization> authorizations;
  private List<ApiDescription> apis;
  private Map<String, Model> models;
  private String description;
  private int position;

  public ApiListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  public ApiListingBuilder basePath(String basePath) {
    this.basePath = basePath;
    return this;
  }

  public ApiListingBuilder resourcePath(String resourcePath) {
    this.resourcePath = resourcePath;
    return this;
  }

  public ApiListingBuilder produces(List<String> produces) {
    this.produces = produces;
    return this;
  }

  public ApiListingBuilder consumes(List<String> consumes) {
    this.consumes = consumes;
    return this;
  }

  public ApiListingBuilder protocol(List<String> protocol) {
    this.protocol = protocol;
    return this;
  }

  public ApiListingBuilder authorizations(List<Authorization> authorizations) {
    this.authorizations = authorizations;
    return this;
  }

  public ApiListingBuilder apis(List<ApiDescription> apis) {
    this.apis = apis;
    return this;
  }

  public ApiListingBuilder models(Map<String, Model> models) {
    this.models = models;
    return this;
  }

  public ApiListingBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ApiListingBuilder position(int position) {
    this.position = position;
    return this;
  }

  public ApiListing build() {
    return new ApiListing(apiVersion, basePath,
            resourcePath, produces, consumes, protocol, authorizations, apis, models, description, position);
  }
}