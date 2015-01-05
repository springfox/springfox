package com.mangofactory.swagger.core;

import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.Group;
import com.mangofactory.service.model.ResourceListing;
import com.mangofactory.service.model.builder.GroupBuilder;
import com.mangofactory.service.model.builder.ResourceListingBuilder;
import com.mangofactory.springmvc.plugins.DocumentationContext;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanResult;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.ApiListingScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.*;
import static com.mangofactory.swagger.controllers.DefaultSwaggerController.*;

@Component
public class SwaggerApiResourceListing {
  private static final Logger log = LoggerFactory.getLogger(SwaggerApiResourceListing.class);


  private ApiListingReferenceScanner apiListingReferenceScanner;
  private ApiListingScanner apiListingScanner;

  @Autowired
  public SwaggerApiResourceListing(
          ApiListingReferenceScanner apiListingReferenceScanner,
          ApiListingScanner apiListingScanner) {

    this.apiListingReferenceScanner = apiListingReferenceScanner;
    this.apiListingScanner = apiListingScanner;
  }

  public Group scan(DocumentationContext context) {
    ApiListingReferenceScanResult result = apiListingReferenceScanner.scan(context);
    List<ApiListingReference> apiListingReferences = result.getApiListingReferences();
    ApiListingScanningContext listingContext = new ApiListingScanningContextBuilder()
            .withDocumenationContext(context)
            .withRequestMappingsByResourceGroup(result.getResourceGroupRequestMappings())
            .build();

    GroupBuilder group = new GroupBuilder()
            .withName(context.getGroupName())
            .withApiListings(apiListingScanner.scan(listingContext));

    Collections.sort(apiListingReferences, context.getListingReferenceOrdering());

    ResourceListing resourceListing = new ResourceListingBuilder()
            .apiVersion(context.getApiInfo().getVersion())
            .apis(apiListingReferences)
            .authorizations(context.getAuthorizationTypes())
            .info(context.getApiInfo())
            .build();

    log.info("Added a resource listing with ({}) api resources: ", apiListingReferences.size());
    for (ApiListingReference apiListingReference : apiListingReferences) {
      String path = apiListingReference.getDescription();

      String prefix = nullToEmpty(path).startsWith("http") ? path : DOCUMENTATION_BASE_PATH;
      log.info("  {} at location: {}{}", path, prefix, apiListingReference.getPath());
    }
    group.withResourceListing(resourceListing);
    return group.build();
  }

}
