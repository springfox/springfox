package com.mangofactory.documentation.service.model.builder;

import com.mangofactory.documentation.service.model.ApiListing;
import com.mangofactory.documentation.service.model.Group;
import com.mangofactory.documentation.service.model.ResourceListing;

import java.util.Map;

import static com.google.common.collect.Maps.*;
import static com.mangofactory.documentation.service.model.builder.BuilderDefaults.*;

public class GroupBuilder {
  private String groupName;
  private Map<String, ApiListing> apiListings = newTreeMap();
  private ResourceListing resourceListing;

  public GroupBuilder name(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  //TODO: Something fishy here... this should just be apiListings since we already know what group it is
  public GroupBuilder apiListingsByGroup(Map<String, ApiListing> apiListings) {
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
