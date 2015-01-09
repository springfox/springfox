package com.mangofactory.spring.web.scanners;

import com.mangofactory.service.model.ApiListingReference;

import java.util.List;
import java.util.Map;

public class ApiListingReferenceScanResult {
  private final List<ApiListingReference> apiListingReferences;
  private final Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings;

  public ApiListingReferenceScanResult(List<ApiListingReference> apiListingReferences,
      Map<ResourceGroup, List<RequestMappingContext>> resourceGroupRequestMappings) {
    this.apiListingReferences = apiListingReferences;
    this.resourceGroupRequestMappings = resourceGroupRequestMappings;
  }

  public List<ApiListingReference> getApiListingReferences() {
    return apiListingReferences;
  }

  public Map<ResourceGroup, List<RequestMappingContext>> getResourceGroupRequestMappings() {
    return resourceGroupRequestMappings;
  }
}
