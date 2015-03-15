package com.mangofactory.documentation.swagger.mappers;

import com.google.common.collect.Maps;
import com.mangofactory.documentation.service.ApiListing;

public class Mappers {
  public static Maps.EntryTransformer<String, ApiListing, com.mangofactory.documentation.swagger.dto.ApiListing>
    toApiListingDto(final ServiceModelToSwaggerMapper mapper) {

    return new Maps.EntryTransformer<String, ApiListing, com.mangofactory.documentation.swagger.dto.ApiListing>() {
      @Override
      public com.mangofactory.documentation.swagger.dto.ApiListing transformEntry(String key, ApiListing value) {
        return mapper.toSwaggerApiListing(value);
      }
    };
  }
}
