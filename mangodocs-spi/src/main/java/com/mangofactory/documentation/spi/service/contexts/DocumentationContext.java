package com.mangofactory.documentation.spi.service.contexts;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.mangofactory.documentation.schema.AlternateTypeRule;
import com.mangofactory.documentation.service.PathProvider;
import com.mangofactory.documentation.service.RequestMappingEvaluator;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiInfo;
import com.mangofactory.documentation.service.model.ApiListingReference;
import com.mangofactory.documentation.service.model.AuthorizationType;
import com.mangofactory.documentation.service.model.Operation;
import com.mangofactory.documentation.service.model.ResponseMessage;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.schema.AlternateTypeProvider;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentationContext {
  private final DocumentationType documentationType;
  private final List<RequestMappingHandlerMapping> handlerMappings;
  private final ApiInfo apiInfo;
  private final String groupName;
  private final RequestMappingEvaluator requestMappingEvaluator;
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

  public DocumentationContext(DocumentationType documentationType,
          List<RequestMappingHandlerMapping> handlerMappings,
          ApiInfo apiInfo, String groupName,
          RequestMappingEvaluator requestMappingEvaluator,
          Set<Class> ignorableParameterTypes,
          Map<RequestMethod, List<ResponseMessage>> globalResponseMessages,
          ResourceGroupingStrategy resourceGroupingStrategy,
          PathProvider pathProvider,
          AuthorizationContext authorizationContext,
          List<AuthorizationType> authorizationTypes,
          List<AlternateTypeRule> alternateTypeRules,
          Ordering<ApiListingReference> listingReferenceOrdering,
          Ordering<ApiDescription> apiDescriptionOrdering,
          Ordering<Operation> operationOrdering) {

    this.documentationType = documentationType;
    this.handlerMappings = handlerMappings;
    this.apiInfo = apiInfo;
    this.groupName = groupName;
    this.requestMappingEvaluator = requestMappingEvaluator;
    this.ignorableParameterTypes = ignorableParameterTypes;
    this.globalResponseMessages = globalResponseMessages;
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    this.pathProvider = pathProvider;
    this.authorizationContext = authorizationContext;
    this.authorizationTypes = authorizationTypes;
    this.listingReferenceOrdering = listingReferenceOrdering;
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    this.operationOrdering = operationOrdering;
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

  public RequestMappingEvaluator getRequestMappingEvaluator() {
    return requestMappingEvaluator;
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
}
