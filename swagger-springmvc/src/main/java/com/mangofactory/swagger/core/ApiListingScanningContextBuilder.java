package com.mangofactory.swagger.core;

import com.mangofactory.springmvc.plugin.DocumentationContext;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.scanners.ResourceGroup;

import java.util.List;
import java.util.Map;

public class ApiListingScanningContextBuilder {
  private DocumentationContext documentationContext;
  private Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup;

  public ApiListingScanningContextBuilder withDocumenationContext(DocumentationContext documentationContext) {
    this.documentationContext = documentationContext;
    return this;
  }

  public ApiListingScanningContextBuilder withRequestMappingsByResourceGroup(
          Map<ResourceGroup, List<RequestMappingContext>> requestMappings) {

    requestMappingsByResourceGroup = requestMappings;
    return this;
  }

  public ApiListingScanningContext build() {
    return new ApiListingScanningContext(documentationContext, requestMappingsByResourceGroup);
  }
}