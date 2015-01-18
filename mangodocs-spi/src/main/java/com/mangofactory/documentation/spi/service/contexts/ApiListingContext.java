package com.mangofactory.documentation.spi.service.contexts;

import com.mangofactory.documentation.service.model.ResourceGroup;
import com.mangofactory.documentation.service.model.builder.ApiListingBuilder;
import com.mangofactory.documentation.spi.DocumentationType;

public class ApiListingContext {
  private final DocumentationType documentationType;
  private final ResourceGroup resourceGroup;
  private ApiListingBuilder apiListingBuilder;

  public ApiListingContext(DocumentationType documentationType,
                           ResourceGroup resourceGroup,
                           ApiListingBuilder apiListingBuilder) {
    this.documentationType = documentationType;
    this.resourceGroup = resourceGroup;
    this.apiListingBuilder = apiListingBuilder;
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public ResourceGroup getResourceGroup() {
    return resourceGroup;
  }

  public ApiListingBuilder apiListingBuilder() {
    return apiListingBuilder;
  }
}
