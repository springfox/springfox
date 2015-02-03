package com.mangofactory.documentation.spring.web.scanners;

import com.mangofactory.documentation.service.ApiListingReference;
import com.mangofactory.documentation.spi.service.contexts.RequestMappingContext;
import com.mangofactory.documentation.service.ResourceGroup;

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
