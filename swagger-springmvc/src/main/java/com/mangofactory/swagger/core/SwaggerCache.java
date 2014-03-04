package com.mangofactory.swagger.core;

import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Component
public class SwaggerCache {
   private Map<String, ResourceListing> swaggerApiResourceListingMap = newLinkedHashMap();

   private Map<String, ApiListing> swaggerApiListingMap = newHashMap();

   public void addSwaggerResourceListing(String swaggerGroup, ResourceListing resourceListing) {
      swaggerApiResourceListingMap.put(swaggerGroup, resourceListing);
   }

   public void addApiListings(Map<String, ApiListing> apiListings) {
     swaggerApiListingMap.putAll(apiListings);
   }

   public ResourceListing getResourceListing(String key){
      return swaggerApiResourceListingMap.get(key);
   }

   public Map<String, ResourceListing> getSwaggerApiResourceListingMap() {
      return swaggerApiResourceListingMap;
   }

   public ApiListing getSwaggerApiListing(String key) {
      return swaggerApiListingMap.get(key);
   }
}
