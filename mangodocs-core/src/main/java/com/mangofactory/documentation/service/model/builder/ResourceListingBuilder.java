package com.mangofactory.documentation.service.model.builder;

import com.google.common.collect.Lists;
import com.mangofactory.documentation.service.model.ApiInfo;
import com.mangofactory.documentation.service.model.ApiListingReference;
import com.mangofactory.documentation.service.model.AuthorizationType;
import com.mangofactory.documentation.service.model.ResourceListing;

import java.util.List;

public class ResourceListingBuilder {
  private String apiVersion;
  private List<ApiListingReference> apis = Lists.newArrayList();
  private List<AuthorizationType> authorizations = Lists.newArrayList();
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