package springdox.documentation.swagger.mappers;

import com.google.common.collect.Maps;
import springdox.documentation.swagger.dto.ApiListing;

public class Mappers {
  public static Maps.EntryTransformer<String, springdox.documentation.service.ApiListing, ApiListing>
    toApiListingDto(final ServiceModelToSwaggerMapper mapper) {

    return new Maps.EntryTransformer<String, springdox.documentation.service.ApiListing, ApiListing>() {
      @Override
      public ApiListing transformEntry(String key, springdox.documentation.service.ApiListing value) {
        return mapper.toSwaggerApiListing(value);
      }
    };
  }
}
