package com.mangofactory.spring.web.plugins;

import com.mangofactory.service.model.builder.ApiListingBuilder;
import com.mangofactory.spring.web.scanners.ResourceGroup;

public class ApiListingContext {
  private final DocumentationContext documentationContext;
  private final ResourceGroup resourceGroup;
  private ApiListingBuilder apiListingBuilder;

  public ApiListingContext(DocumentationContext documentationContext, ResourceGroup resourceGroup, ApiListingBuilder
          apiListingBuilder) {
    this.documentationContext = documentationContext;
    this.resourceGroup = resourceGroup;
    this.apiListingBuilder = apiListingBuilder;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public ResourceGroup getResourceGroup() {
    return resourceGroup;
  }

  public ApiListingBuilder apiListingBuilder() {
    return apiListingBuilder;
  }
}
