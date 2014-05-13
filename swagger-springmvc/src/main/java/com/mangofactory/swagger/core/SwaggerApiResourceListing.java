package com.mangofactory.swagger.core;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.controllers.DefaultSwaggerController;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.ApiListingScanner;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.scanners.ResourceGroup;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.ResourceListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mangofactory.swagger.ScalaUtils.*;

public class SwaggerApiResourceListing {
   private static final Logger log = LoggerFactory.getLogger(SwaggerApiResourceListing.class);

   private SwaggerCache swaggerCache;
   private ApiInfo apiInfo;
   private List<AuthorizationType> authorizationTypes;
   private AuthorizationContext authorizationContext;
   private ApiListingReferenceScanner apiListingReferenceScanner;
   private SwaggerPathProvider swaggerPathProvider;
   private SwaggerGlobalSettings swaggerGlobalSettings;
   private String swaggerGroup;
   private ModelProvider modelProvider;

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

         Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings =
                 apiListingReferenceScanner.getResourceGroupRequestMappings();
         ApiListingScanner apiListingScanner = new ApiListingScanner(resourceGroupRequestMappings, swaggerPathProvider,
                 modelProvider, authorizationContext);
         apiListingScanner.setSwaggerGlobalSettings(swaggerGlobalSettings);
         //DK TODO: Fix this hack!
         apiListingScanner.setResourceGroupingStrategy(apiListingReferenceScanner.getResourceGroupingStrategy());

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


      log.info("Added a resource listing with the ({}) api resources: ", apiListingReferences.size());
      for(ApiListingReference apiListingReference : apiListingReferences){
         String path = fromOption(apiListingReference.description());
         String prefix = path.startsWith("http") ? path :DefaultSwaggerController.DOCUMENTATION_BASE_PATH;
         log.info("  {} at location: {}{}", path, prefix, apiListingReference.path());
      }

      swaggerCache.addSwaggerResourceListing(swaggerGroup, resourceListing);
   }

   public SwaggerCache getSwaggerCache() {
      return swaggerCache;
   }

   public void setSwaggerCache(SwaggerCache swaggerCache) {
      this.swaggerCache = swaggerCache;
   }

   public ApiInfo getApiInfo() {
      return apiInfo;
   }

   public void setApiInfo(ApiInfo apiInfo) {
      this.apiInfo = apiInfo;
   }

   public List<AuthorizationType> getAuthorizationTypes() {
      return authorizationTypes;
   }

   public void setAuthorizationTypes(List<AuthorizationType> authorizationTypes) {
      this.authorizationTypes = authorizationTypes;
   }

   public ApiListingReferenceScanner getApiListingReferenceScanner() {
      return apiListingReferenceScanner;
   }

   public void setApiListingReferenceScanner(ApiListingReferenceScanner apiListingReferenceScanner) {
      this.apiListingReferenceScanner = apiListingReferenceScanner;
   }

   public SwaggerPathProvider getSwaggerPathProvider() {
      return swaggerPathProvider;
   }

   public void setSwaggerPathProvider(SwaggerPathProvider swaggerPathProvider) {
      this.swaggerPathProvider = swaggerPathProvider;
   }

   public SwaggerGlobalSettings getSwaggerGlobalSettings() {
      return swaggerGlobalSettings;
   }

   public void setSwaggerGlobalSettings(SwaggerGlobalSettings swaggerGlobalSettings) {
      this.swaggerGlobalSettings = swaggerGlobalSettings;
   }

   public String getSwaggerGroup() {
      return swaggerGroup;
   }

   public void setSwaggerGroup(String swaggerGroup) {
      this.swaggerGroup = swaggerGroup;
   }

   public void setAuthorizationContext(AuthorizationContext authorizationContext) {
      this.authorizationContext = authorizationContext;
   }

    public void setModelProvider(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }
}
