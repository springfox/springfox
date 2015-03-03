package com.mangofactory.documentation.spring.web.scanners;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.ApiListingReference;
import com.mangofactory.documentation.service.Documentation;
import com.mangofactory.documentation.service.ResourceListing;
import com.mangofactory.documentation.builders.DocumentationBuilder;
import com.mangofactory.documentation.builders.ResourceListingBuilder;
import com.mangofactory.documentation.service.Tag;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ApiDocumentationScanner {

  private ApiListingReferenceScanner apiListingReferenceScanner;
  private ApiListingScanner apiListingScanner;

  @Autowired
  public ApiDocumentationScanner(
          ApiListingReferenceScanner apiListingReferenceScanner,
          ApiListingScanner apiListingScanner) {

    this.apiListingReferenceScanner = apiListingReferenceScanner;
    this.apiListingScanner = apiListingScanner;
  }

  public Documentation scan(DocumentationContext context) {
    ApiListingReferenceScanResult result = apiListingReferenceScanner.scan(context);
    List<ApiListingReference> apiListingReferences = result.getApiListingReferences();
    ApiListingScanningContext listingContext = new ApiListingScanningContext(context, result.getResourceGroupRequestMappings());

    Map<String, ApiListing> apiListings = apiListingScanner.scan(listingContext);
    DocumentationBuilder group = new DocumentationBuilder()
            .name(context.getGroupName())
            .apiListingsByResourceGroupName(apiListings)
            .tags(toTags(apiListings));

    Collections.sort(apiListingReferences, context.getListingReferenceOrdering());

    ResourceListing resourceListing = new ResourceListingBuilder()
            .apiVersion(context.getApiInfo().getVersion())
            .apis(apiListingReferences)
            .authorizations(context.getAuthorizationTypes())
            .info(context.getApiInfo())
            .build();
    group.resourceListing(resourceListing);
    return group.build();
  }

  private Set<Tag> toTags(Map<String, ApiListing> apiListings) {
    if (apiListings == null) {
      return null;
    }
    return FluentIterable
            .from(apiListings.entrySet())
            .transform(fromEntry())
            .toSet();
  }

  private Function<Map.Entry<String, ApiListing>, Tag> fromEntry() {
    return new Function<Map.Entry<String, ApiListing>, Tag>() {
      @Override
      public Tag apply(Map.Entry<String, ApiListing> input) {
        return new Tag(input.getKey(), input.getValue().getDescription());
      }
    };
  }

}
