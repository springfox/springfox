package com.mangofactory.spring.web.ordering;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import com.mangofactory.service.model.ApiListingReference;

/**
 * Orders ApiListingReference's by their position
 */
public class ResourceListingPositionalOrdering extends Ordering<ApiListingReference> {
  @Override
  public int compare(ApiListingReference first, ApiListingReference second) {
    return Ints.compare(first.getPosition(), second.getPosition());
  }
}
