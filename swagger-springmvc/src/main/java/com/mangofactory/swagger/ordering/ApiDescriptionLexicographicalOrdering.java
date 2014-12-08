package com.mangofactory.swagger.ordering;

import com.google.common.collect.Ordering;
import com.mangofactory.swagger.models.dto.ApiDescription;

public class ApiDescriptionLexicographicalOrdering extends Ordering<ApiDescription> {
  @Override
  public int compare(ApiDescription apiDescription, ApiDescription other) {
    return apiDescription.getPath().compareTo(other.getPath());
  }
}
