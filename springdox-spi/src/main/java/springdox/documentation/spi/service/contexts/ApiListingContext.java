package springdox.documentation.spi.service.contexts;

import springdox.documentation.builders.ApiListingBuilder;
import springdox.documentation.service.ResourceGroup;
import springdox.documentation.spi.DocumentationType;

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
