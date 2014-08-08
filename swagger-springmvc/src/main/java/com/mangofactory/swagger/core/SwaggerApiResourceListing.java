package com.mangofactory.swagger.core;

import com.google.common.collect.Ordering;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.controllers.DefaultSwaggerController;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.ApiListingScanner;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.scanners.ResourceGroup;
import com.wordnik.swagger.core.SwaggerSpec;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.ResourceListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
  private String apiVersion = "1";
  private Ordering<ApiListingReference> apiListingReferenceOrdering = new ResourceListingLexicographicalOrdering();
  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;

  public SwaggerApiResourceListing(SwaggerCache swaggerCache, String swaggerGroup) {
    this.swaggerCache = swaggerCache;
    this.swaggerGroup = swaggerGroup;
  }

  public void initialize() {
    List<ApiListingReference> apiListingReferences = new ArrayList<ApiListingReference>();
    if (null != apiListingReferenceScanner) {
      apiListingReferenceScanner.scan();
      apiListingReferences = apiListingReferenceScanner.getApiListingReferences();

      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings =
              apiListingReferenceScanner.getResourceGroupRequestMappings();
      ApiListingScanner apiListingScanner = new ApiListingScanner(resourceGroupRequestMappings, swaggerPathProvider,
              modelProvider, authorizationContext, customAnnotationReaders);

      apiListingScanner.setApiDescriptionOrdering(apiDescriptionOrdering);
      apiListingScanner.setSwaggerGlobalSettings(swaggerGlobalSettings);
      apiListingScanner.setResourceGroupingStrategy(apiListingReferenceScanner.getResourceGroupingStrategy());

      Map<String, ApiListing> apiListings = apiListingScanner.scan();
      swaggerCache.addApiListings(swaggerGroup, apiListings);

    } else {
      log.error("ApiListingReferenceScanner not configured");
    }

    Collections.sort(apiListingReferences, apiListingReferenceOrdering);

    ResourceListing resourceListing = new ResourceListing(
            this.apiVersion,
            SwaggerSpec.version(),
            toScalaList(apiListingReferences),
            toScalaList(authorizationTypes),
            toOption(apiInfo)
    );

    log.info("Added a resource listing with ({}) api resources: ", apiListingReferences.size());
    for (ApiListingReference apiListingReference : apiListingReferences) {
      String path = fromOption(apiListingReference.description());
      String prefix = (path != null && path.startsWith("http")) ? path : DefaultSwaggerController
              .DOCUMENTATION_BASE_PATH;
      log.info("  {} at location: {}{}", path, prefix, apiListingReference.path());
    }

    swaggerCache.addSwaggerResourceListing(swaggerGroup, resourceListing);
  }

  public SwaggerCache getSwaggerCache() {
    return swaggerCache;
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

  public void setAuthorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
  }

  public void setModelProvider(ModelProvider modelProvider) {
    this.modelProvider = modelProvider;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setApiListingReferenceOrdering(Ordering<ApiListingReference> apiListingReferenceOrdering) {
    this.apiListingReferenceOrdering = apiListingReferenceOrdering;
  }

  public void setApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
  }

  public void setCustomAnnotationReaders(Collection<RequestMappingReader> customAnnotationReaders) {
    this.customAnnotationReaders = customAnnotationReaders;
  }
}
