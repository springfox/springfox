package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.ApiInfo;
import com.mangofactory.documentation.service.ApiListingReference;
import com.mangofactory.documentation.service.AuthorizationType;
import com.mangofactory.documentation.service.ResourceListing;

import java.util.List;

import static com.google.common.collect.Lists.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class ResourceListingBuilder {
  private String apiVersion;
  private List<ApiListingReference> apis = newArrayList();
  private List<AuthorizationType> authorizations = newArrayList();
  private ApiInfo info;

  public ResourceListingBuilder apiVersion(String apiVersion) {
    this.apiVersion = defaultIfAbsent(apiVersion, this.apiVersion);
    return this;
  }

  public ResourceListingBuilder apis(List<ApiListingReference> apis) {
    this.apis.addAll(nullToEmptyList(apis));
    return this;
  }

  public ResourceListingBuilder authorizations(List<AuthorizationType> authorizations) {
    this.authorizations.addAll(nullToEmptyList(authorizations));
    return this;
  }

  public ResourceListingBuilder info(ApiInfo info) {
    this.info = defaultIfAbsent(info, this.info);
    return this;
  }

  public ResourceListing build() {
    return new ResourceListing(apiVersion, apis, authorizations, info);
  }
}