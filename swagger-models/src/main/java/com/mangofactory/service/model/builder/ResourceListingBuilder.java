package com.mangofactory.service.model.builder;

import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResourceListing;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class ResourceListingBuilder {
  private String apiVersion;
  private List<ApiListingReference> apis = newArrayList();
  private List<AuthorizationType> authorizations = newArrayList();
  private ApiInfo info;

  public ResourceListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  public ResourceListingBuilder apis(List<ApiListingReference> apis) {
    this.apis = apis;
    return this;
  }

  public ResourceListingBuilder authorizations(List<AuthorizationType> authorizations) {
    this.authorizations = authorizations;
    return this;
  }

  public ResourceListingBuilder info(ApiInfo info) {
    this.info = info;
    return this;
  }

  public ResourceListing build() {
    return new ResourceListing(apiVersion, apis, authorizations, info);
  }
}