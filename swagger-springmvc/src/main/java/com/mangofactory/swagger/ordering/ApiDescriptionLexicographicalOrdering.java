package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.mangofactory.service.model.ApiDescription;

public class ApiDescriptionLexicographicalOrdering extends Ordering<ApiDescription> {
  @Override
  public int compare(ApiDescription apiDescription, ApiDescription other) {
    return apiDescription.getPath().compareTo(other.getPath());
  }
}
