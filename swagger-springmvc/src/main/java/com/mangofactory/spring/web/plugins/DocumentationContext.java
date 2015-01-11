package com.mangofactory.spring.web.plugins;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.spring.web.RequestMappingEvaluator;
import com.mangofactory.spring.web.ResourceGroupingStrategy;
import com.mangofactory.spring.web.PathProvider;
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
  /**
   * Set of classes to exclude from spring controller request mapping methods
   * e.g HttpServletRequest, BindingResult
   */
  private Set<Class> ignorableParameterTypes;

  /**
   * Map of spring RequestMethod's to a list of http status codes and accompanying messages
   *
   * @see com.mangofactory.spring.web.readers.operation.ResponseMessagesReader
   */
  private final Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private final ResourceGroupingStrategy resourceGroupingStrategy;
  private final PathProvider pathProvider;
  private final AuthorizationContext authorizationContext;
  private final List<AuthorizationType> authorizationTypes;
  private final Ordering<ApiListingReference> listingReferenceOrdering;
  private final Ordering<ApiDescription> apiDescriptionOrdering;

  public DocumentationContext(DocumentationType documentationType, List<RequestMappingHandlerMapping> handlerMappings,
                              ApiInfo apiInfo, String groupName,
                              RequestMappingEvaluator requestMappingEvaluator,
                              Set<Class> ignorableParameterTypes,
                              Map<RequestMethod, List<ResponseMessage>> globalResponseMessages,
                              ResourceGroupingStrategy resourceGroupingStrategy,
                              PathProvider pathProvider,
                              AuthorizationContext authorizationContext,
                              List<AuthorizationType> authorizationTypes,
                              Ordering<ApiListingReference> listingReferenceOrdering,
                              Ordering<ApiDescription> apiDescriptionOrdering) {
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
}
