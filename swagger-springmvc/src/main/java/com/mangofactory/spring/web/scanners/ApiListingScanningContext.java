package com.mangofactory.spring.web.scanners;

import com.mangofactory.spring.web.plugins.DocumentationContext;

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
