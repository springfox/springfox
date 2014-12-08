package com.mangofactory.swagger.core;

import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.ResourceListing;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Component
public class SwaggerCache {
  private Map<String, ResourceListing> swaggerApiResourceListingMap = newLinkedHashMap();

  //Map<'swaggerGroup', Map<controllerGroupName>, ApiListing>>
  private Map<String, Map<String, ApiListing>> swaggerApiListingMap = newLinkedHashMap();

  public void addSwaggerResourceListing(String swaggerGroup, ResourceListing resourceListing) {
    swaggerApiResourceListingMap.put(swaggerGroup, resourceListing);
  }

  public void addApiListings(String swaggerGroup, Map<String, ApiListing> apiListings) {
    swaggerApiListingMap.put(swaggerGroup, apiListings);
  }

  public ResourceListing getResourceListing(String key) {
    return swaggerApiResourceListingMap.get(key);
  }

  public Map<String, ResourceListing> getSwaggerApiResourceListingMap() {
    return swaggerApiResourceListingMap;
  }

  public Map<String, Map<String, ApiListing>> getSwaggerApiListingMap() {
    return swaggerApiListingMap;
  }
}
