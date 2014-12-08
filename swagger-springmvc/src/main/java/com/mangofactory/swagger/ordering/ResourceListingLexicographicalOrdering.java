package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.mangofactory.swagger.models.dto.ApiListingReference;

/**
 * Orders ApiListingReference's Lexicographically  by their paths
 */
public class ResourceListingLexicographicalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return first.getPath().compareTo(second.getPath());
  }
}
