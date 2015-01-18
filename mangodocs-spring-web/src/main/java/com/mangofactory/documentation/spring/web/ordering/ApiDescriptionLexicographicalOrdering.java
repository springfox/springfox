package com.mangofactory.documentation.spring.web.ordering;

import com.google.common.collect.Ordering;
import com.mangofactory.documentation.service.model.ApiDescription;

public class ApiDescriptionLexicographicalOrdering extends Ordering<ApiDescription> {
  @Override
  public int compare(ApiDescription apiDescription, ApiDescription other) {
    return apiDescription.getPath().compareTo(other.getPath());
  }
}
