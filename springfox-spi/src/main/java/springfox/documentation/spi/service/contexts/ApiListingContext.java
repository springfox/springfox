package springfox.documentation.spi.service.contexts;

import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.DocumentationType;

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
