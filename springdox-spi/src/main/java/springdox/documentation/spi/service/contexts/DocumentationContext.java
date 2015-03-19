package springdox.documentation.spi.service.contexts;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springdox.documentation.PathProvider;
import springdox.documentation.schema.AlternateTypeRule;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.service.ApiInfo;
import springdox.documentation.service.ApiListingReference;
import springdox.documentation.service.AuthorizationType;
import springdox.documentation.service.Operation;
import springdox.documentation.service.ResponseMessage;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.schema.AlternateTypeProvider;
import springdox.documentation.spi.service.ResourceGroupingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentationContext {
  private final DocumentationType documentationType;
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private final ApiInfo apiInfo;
  private final String groupName;
  private final ApiSelector apiSelector;
  private final AlternateTypeProvider alternateTypeProvider;
  private final Set<Class> ignorableParameterTypes;
  private final Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private final ResourceGroupingStrategy resourceGroupingStrategy;
  private final PathProvider pathProvider;
  private final AuthorizationContext authorizationContext;
  private final List<AuthorizationType> authorizationTypes;
  private final Ordering<ApiListingReference> listingReferenceOrdering;
  private final Ordering<ApiDescription> apiDescriptionOrdering;
  private final Ordering<Operation> operationOrdering;
  private Set<String> produces;
  private Set<String> consumes;
  private Set<String> protocols;

  public DocumentationContext(DocumentationType documentationType,
                              List<RequestMappingHandlerMapping> handlerMappings,
                              ApiInfo apiInfo, String groupName,
                              ApiSelector apiSelector,
                              Set<Class> ignorableParameterTypes,
                              Map<RequestMethod, List<ResponseMessage>> globalResponseMessages,
                              ResourceGroupingStrategy resourceGroupingStrategy,
                              PathProvider pathProvider,
                              AuthorizationContext authorizationContext,
                              List<AuthorizationType> authorizationTypes,
                              List<AlternateTypeRule> alternateTypeRules,
                              Ordering<ApiListingReference> listingReferenceOrdering,
                              Ordering<ApiDescription> apiDescriptionOrdering,
                              Ordering<Operation> operationOrdering, 
                              Set<String> produces, 
                              Set<String> consumes, 
                              Set<String> protocols) {

    this.documentationType = documentationType;
    this.handlerMappings = handlerMappings;
    this.apiInfo = apiInfo;
    this.groupName = groupName;
    this.apiSelector = apiSelector;
    this.ignorableParameterTypes = ignorableParameterTypes;
    this.globalResponseMessages = globalResponseMessages;
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    this.pathProvider = pathProvider;
    this.authorizationContext = authorizationContext;
    this.authorizationTypes = authorizationTypes;
    this.listingReferenceOrdering = listingReferenceOrdering;
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    this.operationOrdering = operationOrdering;
    this.produces = produces;
    this.consumes = consumes;
    this.protocols = protocols;
    this.alternateTypeProvider = new AlternateTypeProvider(alternateTypeRules);
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public List<RequestMappingHandlerMapping> getHandlerMappings() {
    return handlerMappings;
  }

  public ApiInfo getApiInfo() {
    return apiInfo;
  }

  public String getGroupName() {
    return groupName;
  }

  public ApiSelector getApiSelector() {
    return apiSelector;
  }

  public ImmutableSet<Class> getIgnorableParameterTypes() {
    return ImmutableSet.copyOf(ignorableParameterTypes);
  }

  public Map<RequestMethod, List<ResponseMessage>> getGlobalResponseMessages() {
    return globalResponseMessages;
  }

  public ResourceGroupingStrategy getResourceGroupingStrategy() {
    return resourceGroupingStrategy;
  }

  public PathProvider getPathProvider() {
    return pathProvider;
  }

  public AuthorizationContext getAuthorizationContext() {
    return authorizationContext;
  }

  public List<AuthorizationType> getAuthorizationTypes() {
    return authorizationTypes;
  }

  public Ordering<ApiListingReference> getListingReferenceOrdering() {
    return listingReferenceOrdering;
  }

  public Ordering<ApiDescription> getApiDescriptionOrdering() {
    return apiDescriptionOrdering;
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return alternateTypeProvider;
  }

  public Ordering<Operation> operationOrdering() {
    return operationOrdering;
  }

  public Set<String> getProduces() {
    return produces;
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public Set<String> getProtocols() {
    return protocols;
  }
}
