package com.mangofactory.documentation.spring.web.scanners;

import com.mangofactory.documentation.service.ApiListingReference;
import com.mangofactory.documentation.service.Group;
import com.mangofactory.documentation.service.ResourceListing;
import com.mangofactory.documentation.builders.GroupBuilder;
import com.mangofactory.documentation.builders.ResourceListingBuilder;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ApiGroupScanner {

  private ApiListingReferenceScanner apiListingReferenceScanner;
  private ApiListingScanner apiListingScanner;

  @Autowired
  public ApiGroupScanner(
          ApiListingReferenceScanner apiListingReferenceScanner,
          ApiListingScanner apiListingScanner) {

    this.apiListingReferenceScanner = apiListingReferenceScanner;
    this.apiListingScanner = apiListingScanner;
  }

  public Group scan(DocumentationContext context) {
    ApiListingReferenceScanResult result = apiListingReferenceScanner.scan(context);
    List<ApiListingReference> apiListingReferences = result.getApiListingReferences();
    ApiListingScanningContext listingContext = new ApiListingScanningContext(context, result.getResourceGroupRequestMappings());

    GroupBuilder group = new GroupBuilder()
            .name(context.getGroupName())
            .apiListingsByGroup(apiListingScanner.scan(listingContext));

    Collections.sort(apiListingReferences, context.getListingReferenceOrdering());

    ResourceListing resourceListing = new ResourceListingBuilder()
            .apiVersion(context.getApiInfo().getVersion())
            .apis(apiListingReferences)
            .authorizations(context.getAuthorizationTypes())
            .info(context.getApiInfo())
            .build();
// Removed this as this was purely for logging
//    log.info("Added a resource listing with ({}) api resources: ", apiListingReferences.size());
//    for (ApiListingReference apiListingReference : apiListingReferences) {
//      String path = apiListingReference.getDescription();
//
//      String prefix = nullToEmpty(path).startsWith("http") ? path : DOCUMENTATION_BASE_PATH;
//      log.info("  {} at location: {}{}", path, prefix, apiListingReference.getPath());
//    }
    group.resourceListing(resourceListing);
    return group.build();
  }

}
