package com.mangofactory.servicemodel.builder;

import com.mangofactory.servicemodel.ApiInfo;
import com.mangofactory.servicemodel.ApiListingReference;
import com.mangofactory.servicemodel.AuthorizationType;
import com.mangofactory.servicemodel.ResourceListing;

import java.util.List;

public class ResourceListingBuilder {
  private String apiVersion;
  private String swaggerVersion;
  private List<ApiListingReference> apis;
  private List<AuthorizationType> authorizations;
  private ApiInfo info;

  public ResourceListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
    return this;
  }

  public ResourceListingBuilder swaggerVersion(String swaggerVersion) {
    this.swaggerVersion = swaggerVersion;
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
    return new ResourceListing(apiVersion, swaggerVersion, apis, authorizations, info);
  }
}