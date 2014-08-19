package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.wordnik.swagger.model.ApiListingReference;

/**
 * Orders ApiListingReference's Lexicographically  by their paths
 */
public class ResourceListingLexicographicalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return first.path().compareTo(second.path());
  }
}
