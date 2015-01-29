package com.mangofactory.documentation.service.model.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Authorization;
import com.mangofactory.documentation.schema.Model;

import java.util.List;
import java.util.Map;

public class ApiListingBuilder {
  private final Ordering<ApiDescription> descriptionOrdering;
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private List<String> produces = Lists.newArrayList();
  private List<String> consumes = Lists.newArrayList();
  private List<String> protocol;
  private List<Authorization> authorizations;
  private List<ApiDescription> apis;
  private Map<String, Model> models;
  private String description;
  private int position;

  public ApiListingBuilder(Ordering<ApiDescription> descriptionOrdering) {
    this.descriptionOrdering = descriptionOrdering;
  }

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
    this.produces = Lists.newArrayList(produces);
    return this;
  }

  public ApiListingBuilder consumes(List<String> consumes) {
    this.consumes = Lists.newArrayList(consumes);
    return this;
  }

  public ApiListingBuilder appendProduces(List<String> produces) {
    this.produces.addAll(produces);
    return this;
  }

  public ApiListingBuilder appendConsumes(List<String> consumes) {
    this.consumes.addAll(consumes);
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
    this.apis = descriptionOrdering.sortedCopy(apis);
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