package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Group;
import com.mangofactory.documentation.service.model.ResourceListing;

import java.util.Map;

import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class GroupBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings;
  private ResourceListing resourceListing;

  public GroupBuilder withName(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }


  public GroupBuilder withApiListings(Map<String, ApiListing> apiListings) {
    this.apiListings = apiListings;
    return this;
  }

  public GroupBuilder withResourceListing(ResourceListing resourceListing) {
    this.resourceListing = defaultIfAbsent(resourceListing, this.resourceListing);
    return this;
  }

  public Group build() {
    return new Group(groupName, apiListings, resourceListing);
  }
}
