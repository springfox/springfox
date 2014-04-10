package com.mangofactory.swagger.core;

import com.fasterxml.classmate.TypeResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.models.AccessorsProvider;
import com.mangofactory.swagger.models.DefaultModelPropertiesProvider;
import com.mangofactory.swagger.models.DefaultModelProvider;
import com.mangofactory.swagger.models.FieldsProvider;
import com.mangofactory.swagger.models.ModelDependencyProvider;
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

          TypeResolver typeResolver = swaggerGlobalSettings.getTypeResolver();
          DefaultModelPropertiesProvider propertiesProvider
                  = new DefaultModelPropertiesProvider(new ObjectMapper(), //DK TODO: Autowire this
                        new AccessorsProvider(typeResolver),  new FieldsProvider(typeResolver));
          ModelDependencyProvider dependencyProvider = new ModelDependencyProvider(typeResolver, propertiesProvider);
          DefaultModelProvider modelProvider = new DefaultModelProvider(typeResolver, propertiesProvider,
                  dependencyProvider);
          ApiListingScanner apiListingScanner = new ApiListingScanner(resourceGroupRequestMappings, swaggerPathProvider,
                  modelProvider, authorizationContext);
         apiListingScanner.setSwaggerGlobalSettings(swaggerGlobalSettings);
          //DK TODO: Fix this hack!
          apiListingScanner.setControllerNamingStrategy(apiListingReferenceScanner.getResourceGroupingStrategy());

         Map<String, ApiListing> apiListings = apiListingScanner.scan();
         swaggerCache.addApiListings(apiListings);

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
}
