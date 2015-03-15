package springdox.documentation.spring.web.scanners;

import com.google.common.collect.Ordering;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.service.ResourceGroup;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.contexts.DocumentationContext;
import springdox.documentation.spi.service.contexts.RequestMappingContext;

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

  public Ordering<ApiDescription> apiDescriptionOrdering() {
    return documentationContext.getApiDescriptionOrdering();
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }
}
