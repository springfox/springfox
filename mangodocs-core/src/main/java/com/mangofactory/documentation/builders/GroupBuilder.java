package com.mangofactory.documentation.builders;

import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.Group;
import com.mangofactory.documentation.service.ResourceListing;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.builders.BuilderDefaults.*;

public class GroupBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings = newTreeMap();
  private ResourceListing resourceListing;

  public GroupBuilder name(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  public GroupBuilder apiListingsByResourceGroupName(Map<String, ApiListing> apiListings) {
    if (apiListings != null) {
      this.apiListings.putAll(apiListings);
    }
    return this;
  }

  public GroupBuilder resourceListing(ResourceListing resourceListing) {
    this.resourceListing = defaultIfAbsent(resourceListing, this.resourceListing);
    return this;
  }

  public Group build() {
    return new Group(groupName, apiListings, resourceListing);
  }
}
