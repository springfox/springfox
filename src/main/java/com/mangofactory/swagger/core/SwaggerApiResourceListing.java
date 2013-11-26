package com.mangofactory.swagger.core;

import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.ApiListingScanner;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.ScalaUtils.toOption;
import static com.mangofactory.swagger.ScalaUtils.toScalaList;

@Slf4j
public class SwaggerApiResourceListing {

   @Getter
   private SwaggerCache swaggerCache;

   @Getter
   @Setter
   private ApiInfo apiInfo;
   @Getter
   @Setter
   private List<AuthorizationType> authorizationTypes;

   @Setter
   private ApiListingReferenceScanner apiListingReferenceScanner;

   @Setter
   private SwaggerPathProvider swaggerPathProvider;

   private String swaggerGroup;

   public SwaggerApiResourceListing(SwaggerCache swaggerCache, String swaggerGroup) {
      this.swaggerCache = swaggerCache;
      this.swaggerGroup = swaggerGroup;
   }

   @PostConstruct
   public void initialize() {
      List<ApiListingReference> apiListingReferences = new ArrayList<ApiListingReference>();
      if (null != apiListingReferenceScanner) {
         apiListingReferenceScanner.scan();
         apiListingReferences = apiListingReferenceScanner.getApiListingReferences();

         Map<String, List<RequestMappingContext>> resourceGroupRequestMappings =
               apiListingReferenceScanner.getResourceGroupRequestMappings();

         ApiListingScanner apiListingScanner = new ApiListingScanner(
               resourceGroupRequestMappings, apiListingReferenceScanner.getResourceGroup(), swaggerPathProvider);

         Map<String, ApiListing> apiListings = apiListingScanner.scan();
         swaggerCache.addApiListings(swaggerGroup, apiListings);

      } else {
         log.error("ApiListingReferenceScanner not configured");
      }
      ResourceListing resourceListing = new ResourceListing(
            "1",
            SwaggerSpec.version(),
            toScalaList(apiListingReferences),
            toScalaList(authorizationTypes),
            toOption(apiInfo)
      );

      swaggerCache.addSwaggerResourceListing(swaggerGroup, resourceListing);
   }


}
