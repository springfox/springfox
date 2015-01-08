package com.mangofactory.springmvc.plugins;

import com.google.common.collect.Ordering;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.core.RequestMappingEvaluator;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
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
   * @see com.mangofactory.swagger.readers.operation.ResponseMessagesReader
   */
  private final Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private final ResourceGroupingStrategy resourceGroupingStrategy;
  private final SwaggerPathProvider swaggerPathProvider;
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
                              SwaggerPathProvider swaggerPathProvider,
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
    this.swaggerPathProvider = swaggerPathProvider;
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

  public Set<Class> getIgnorableParameterTypes() {
    return ignorableParameterTypes;
  }

  public Map<RequestMethod, List<ResponseMessage>> getGlobalResponseMessages() {
    return globalResponseMessages;
  }

  public ResourceGroupingStrategy getResourceGroupingStrategy() {
    return resourceGroupingStrategy;
  }

  public SwaggerPathProvider getSwaggerPathProvider() {
    return swaggerPathProvider;
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
