package com.mangofactory.swagger.dto.mappers;

import com.google.common.collect.Maps;
import com.mangofactory.service.model.ApiListing;

public class Mappers {
  public static Maps.EntryTransformer<String, ApiListing, com.mangofactory.swagger.dto.ApiListing>
    toApiListingDto(final ServiceModelToSwaggerMapper mapper) {

    return new Maps.EntryTransformer<String, ApiListing, com.mangofactory.swagger.dto.ApiListing>() {
      @Override
      public com.mangofactory.swagger.dto.ApiListing transformEntry(String key, ApiListing value) {
        return mapper.toSwagger(value);
      }
    };
  }
}
