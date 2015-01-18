package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Group;
import com.mangofactory.documentation.service.model.ResourceListing;

import java.util.Map;

public class GroupBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings;
  private ResourceListing resourceListing;

  public GroupBuilder withName(String groupName) {
    this.groupName = groupName;
    return this;
  }


  public GroupBuilder withApiListings(Map<String, ApiListing> apiListings) {
    this.apiListings = apiListings;
    return this;
  }

  public GroupBuilder withResourceListing(ResourceListing resourceListing) {
    this.resourceListing = resourceListing;
    return this;
  }

  public Group build() {
    return new Group(groupName, apiListings, resourceListing);
  }
}
