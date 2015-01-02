package com.mangofactory.swagger.core;

import com.mangofactory.springmvc.plugin.DocumentationContext;
import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.scanners.ResourceGroup;

import java.util.List;
import java.util.Map;

public class ApiListingScanningContext {
  private final DocumentationContext documentationContext;
  private final Map<ResourceGroup, List<RequestMappingContext>> requestMappingsByResourceGroup;

  public ApiListingScanningContext(DocumentationContext documentationContext,
                                   Map<ResourceGroup, List<RequestMappingContext>>  requestMappingsByResourceGroup) {
    this.documentationContext = documentationContext;
    this.requestMappingsByResourceGroup = requestMappingsByResourceGroup;
  }

  public Map<ResourceGroup, List<RequestMappingContext>> getRequestMappingsByResourceGroup() {
    return requestMappingsByResourceGroup;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }
}
