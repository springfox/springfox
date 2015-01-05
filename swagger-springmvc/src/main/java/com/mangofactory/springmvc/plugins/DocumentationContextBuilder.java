package com.mangofactory.springmvc.plugins;

import com.google.common.collect.Ordering;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.core.RequestMappingEvaluator;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentationContextBuilder {
  private final Defaults defaults;
  private List<RequestMappingHandlerMapping> handlerMappings;
  private ApiInfo apiInfo;
  private String groupName;
  private RequestMappingEvaluator requestMappingEvaluator;
  private Set<Class> ignorableParameterTypes;
  private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private SwaggerPathProvider swaggerPathProvider;
  private AuthorizationContext authorizationContext;
  private List<AuthorizationType> authorizationTypes;
  private Ordering<ApiListingReference> listingReferenceOrdering;
  private Ordering<ApiDescription> apiDescriptionOrdering;

  DocumentationContextBuilder(Defaults defaults) {
    this.defaults = defaults;
  }


  public Defaults getDefaults() {
    return defaults;
  }

  public DocumentationContextBuilder withHandlerMappings(List<RequestMappingHandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
    return this;
  }

  public DocumentationContextBuilder withApiInfo(ApiInfo apiInfo) {
    this.apiInfo = apiInfo;
    return this;
  }

  public DocumentationContextBuilder withGroupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  public DocumentationContextBuilder withRequestMappingEvaluator(RequestMappingEvaluator requestMappingEvaluator) {
    this.requestMappingEvaluator = requestMappingEvaluator;
    return this;
  }

  public DocumentationContextBuilder withIgnorableParameterTypes(Set<Class> ignorableParameterTypes) {
    this.ignorableParameterTypes = ignorableParameterTypes;
    return this;
  }

  public DocumentationContextBuilder withGlobalResponseMessages(Map<RequestMethod, List<ResponseMessage>>
                                                                        globalResponseMessages) {
    this.globalResponseMessages = globalResponseMessages;
    return this;
  }

  public DocumentationContextBuilder withResourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    return this;
  }

  public DocumentationContextBuilder withSwaggerPathProvider(SwaggerPathProvider swaggerPathProvider) {
    this.swaggerPathProvider = swaggerPathProvider;
    return this;
  }

  public DocumentationContext build() {
    return new DocumentationContext(handlerMappings, apiInfo, groupName, requestMappingEvaluator,
            ignorableParameterTypes, globalResponseMessages, resourceGroupingStrategy, swaggerPathProvider,
            authorizationContext, authorizationTypes, listingReferenceOrdering, apiDescriptionOrdering);
  }

  public DocumentationContextBuilder withAuthorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
    return this;
  }

  public DocumentationContextBuilder withAuthorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  public DocumentationContextBuilder withApiListingReferenceOrdering(Ordering<ApiListingReference>
                                                                             listingReferenceOrdering) {
    this.listingReferenceOrdering = listingReferenceOrdering;
    return this;
  }

  public DocumentationContextBuilder withApiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    return this;
  }

  public DocumentationContextBuilder withExcludedAnnotations(List<Class<? extends Annotation>> excludedAnnotations) {
    return this;
  }
}