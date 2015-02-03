package com.mangofactory.documentation.builders;

import com.google.common.collect.Ordering;
import com.mangofactory.documentation.schema.Model;
import com.mangofactory.documentation.service.ApiDescription;
import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.Authorization;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ApiListingBuilder {
  private final Ordering<ApiDescription> descriptionOrdering;
  private String apiVersion;
  private String basePath;
  private String resourcePath;
  private String description;
  private int position;

  private List<String> produces = newArrayList();
  private List<String> consumes = newArrayList();
  private List<String> protocol = newArrayList();
  private List<Authorization> authorizations = newArrayList();
  private List<ApiDescription> apis = newArrayList();
  private Map<String, Model> models = newHashMap();

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
    this.resourcePath = defaultIfAbsent(resourcePath, this.resourcePath);
    return this;
  }

  public ApiListingBuilder produces(List<String> produces) {
    if (produces != null) {
      this.produces = newArrayList(produces);
    }
    return this;
  }

  public ApiListingBuilder consumes(List<String> consumes) {
    if (consumes != null) {
      this.consumes = newArrayList(consumes);
    }
    return this;
  }

  public ApiListingBuilder appendProduces(List<String> produces) {
    this.produces.addAll(nullToEmptyList(produces));
    return this;
  }

  public ApiListingBuilder appendConsumes(List<String> consumes) {
    this.consumes.addAll(consumes);
    return this;
  }

  public ApiListingBuilder protocols(List<String> protocols) {
    if (protocols != null) {
      this.protocol.addAll(protocols);
    }
    return this;
  }

  public ApiListingBuilder authorizations(List<Authorization> authorizations) {
    if (authorizations != null) {
      this.authorizations = newArrayList(authorizations);
    }
    return this;
  }

  public ApiListingBuilder apis(List<ApiDescription> apis) {
    if (apis != null) {
      this.apis = descriptionOrdering.sortedCopy(apis);
    }
    return this;
  }

  public ApiListingBuilder models(Map<String, Model> models) {
    if (models != null) {
      this.models.putAll(models);
    }
    return this;
  }

  public ApiListingBuilder description(String description) {
    this.description = defaultIfAbsent(description, this.description);
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