package com.mangofactory.documentation.service.model.builder;

import com.google.common.collect.Ordering;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Authorization;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class ApiListingBuilder {
  private final Ordering<ApiDescription> descriptionOrdering;
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private List<String> produces = newArrayList();
  private List<String> consumes = newArrayList();
  private List<String> protocol = newArrayList();
  private List<Authorization> authorizations;
  private List<ApiDescription> apis;
  private Map<String, Model> models;
  private String description;
  private int position;

  public ApiListingBuilder(Ordering<ApiDescription> descriptionOrdering) {
    this.descriptionOrdering = descriptionOrdering;
  }

  public ApiListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = defaultIfAbsent(apiVersion, this.apiVersion);
    return this;
  }

  public ApiListingBuilder basePath(String basePath) {
    this.basePath = defaultIfAbsent(basePath, this.basePath);
    return this;
  }

  public ApiListingBuilder resourcePath(String resourcePath) {
    this.resourcePath = defaultIfAbsent(resourcePath, resourcePath);
    return this;
  }

  public ApiListingBuilder produces(List<String> produces) {
    this.produces = newArrayList(produces);
    return this;
  }

  public ApiListingBuilder consumes(List<String> consumes) {
    this.consumes = newArrayList(consumes);
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
    this.protocol.addAll(nullToEmptyList(protocol));
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