package com.mangofactory.swagger.core;

import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.ApiListingScanner;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.scanners.ResourceGroup;
import com.wordnik.swagger.models.Info;
import com.wordnik.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SwaggerApiResourceListing {
  private static final Logger log = LoggerFactory.getLogger(SwaggerApiResourceListing.class);

  private SwaggerCache swaggerCache;
  private Info apiInfo;
//  private List<AuthorizationType> authorizationTypes;
  private AuthorizationContext authorizationContext;
  private ApiListingReferenceScanner apiListingReferenceScanner;
  private SwaggerPathProvider swaggerPathProvider;
  private SwaggerGlobalSettings swaggerGlobalSettings;
  private String swaggerGroup;
  private ModelProvider modelProvider;
  private String apiVersion = "1";
//  private Ordering<ApiListingReference> apiListingReferenceOrdering = new ResourceListingLexicographicalOrdering();
//  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private Collection<RequestMappingReader> customAnnotationReaders;

  public SwaggerApiResourceListing(SwaggerCache swaggerCache, String swaggerGroup) {
    this.swaggerCache = swaggerCache;
    this.swaggerGroup = swaggerGroup;
  }

  public void initialize() {
    if (null != apiListingReferenceScanner) {
//      apiListingReferenceScanner.scan();
//      apiListingReferences = apiListingReferenceScanner.getApiListingReferences();

      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings =
              apiListingReferenceScanner.getResourceGroupRequestMappings();
      ApiListingScanner apiListingScanner = new ApiListingScanner(resourceGroupRequestMappings, swaggerPathProvider,
              modelProvider, authorizationContext, customAnnotationReaders);

//      apiListingScanner.setApiDescriptionOrdering(apiDescriptionOrdering);
      apiListingScanner.setSwaggerGlobalSettings(swaggerGlobalSettings);
      apiListingScanner.setResourceGroupingStrategy(apiListingReferenceScanner.getResourceGroupingStrategy());

//      Map<String, ApiListing> apiListings = apiListingScanner.scan();
//      swaggerCache.addApiListings(swaggerGroup, apiListings);

    } else {
      log.error("ApiListingReferenceScanner not configured");
    }

//    Collections.sort(apiListingReferences, apiListingReferenceOrdering);
    Swagger swagger = new Swagger();
    swagger.setInfo(this.apiInfo);

//    ResourceListing resourceListing = new ResourceListing(
//            this.apiVersion,
//            SwaggerSpec.version(),
//            toScalaList(apiListingReferences),
//            toScalaList(authorizationTypes),
//            toOption(apiInfo)
//    );

//    log.info("Added a resource listing with ({}) api resources: ", apiListingReferences.size());
//    for (ApiListingReference apiListingReference : apiListingReferences) {
//      String path = fromOption(apiListingReference.description());
//      String prefix = (path != null && path.startsWith("http")) ? path : DefaultSwaggerController
//              .DOCUMENTATION_BASE_PATH;
//      log.info("  {} at location: {}{}", path, prefix, apiListingReference.path());
//    }

    swaggerCache.addSwaggerApi(swaggerGroup, swagger);
  }

  public SwaggerCache getSwaggerCache() {
    return swaggerCache;
  }

  public void setApiInfo(Info apiInfo) {
    this.apiInfo = apiInfo;
  }

//  public List<AuthorizationType> getAuthorizationTypes() {
//    return authorizationTypes;
//  }

//  public void setAuthorizationTypes(List<AuthorizationType> authorizationTypes) {
//    this.authorizationTypes = authorizationTypes;
//  }

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

//  public void setApiListingReferenceOrdering(Ordering<ApiListingReference> apiListingReferenceOrdering) {
//    this.apiListingReferenceOrdering = apiListingReferenceOrdering;
//  }

//  public void setApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
//    this.apiDescriptionOrdering = apiDescriptionOrdering;
//  }

  public void setCustomAnnotationReaders(Collection<RequestMappingReader> customAnnotationReaders) {
    this.customAnnotationReaders = customAnnotationReaders;
  }
}
