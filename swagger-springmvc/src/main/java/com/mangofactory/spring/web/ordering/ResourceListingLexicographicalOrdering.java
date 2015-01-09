package com.mangofactory.spring.web.ordering;

import com.google.common.collect.Ordering;
import com.mangofactory.service.model.ApiListingReference;

/**
 * Orders ApiListingReference's Lexicographically  by their paths
 */
public class ResourceListingLexicographicalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return first.getPath().compareTo(second.getPath());
  }
}
