package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.wordnik.swagger.model.ApiListingReference;

/**
 * Orders ApiListingReference's by their position
 */
public class ResourceListingPositionalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return Ints.compare(first.position(), second.position());
  }
}
