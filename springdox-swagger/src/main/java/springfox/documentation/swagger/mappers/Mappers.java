package springfox.documentation.swagger.mappers;

import com.google.common.collect.Maps;
import springfox.documentation.swagger.dto.ApiListing;

public class Mappers {
  public static Maps.EntryTransformer<String, springfox.documentation.service.ApiListing, ApiListing>
    toApiListingDto(final ServiceModelToSwaggerMapper mapper) {

    return new Maps.EntryTransformer<String, springfox.documentation.service.ApiListing, ApiListing>() {
      @Override
      public ApiListing transformEntry(String key, springfox.documentation.service.ApiListing value) {
        return mapper.toSwaggerApiListing(value);
      }
    };
  }
}
