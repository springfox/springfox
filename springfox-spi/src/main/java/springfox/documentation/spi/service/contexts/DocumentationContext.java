/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spi.service.contexts;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.service.ResourceGroupingStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentationContext {
  private final DocumentationType documentationType;
  private final List<RequestHandler> handlerMappings;
  private final ApiInfo apiInfo;
  private final String groupName;
  private final ApiSelector apiSelector;
  private final AlternateTypeProvider alternateTypeProvider;
  private final Set<Class> ignorableParameterTypes;
  private final Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private final List<Parameter> globalOperationParameters;
  private final ResourceGroupingStrategy resourceGroupingStrategy;
  private final PathProvider pathProvider;
  private final List<SecurityContext> securityContexts;
  private final List<? extends SecurityScheme> securitySchemes;
  private final Ordering<ApiListingReference> listingReferenceOrdering;
  private final Ordering<ApiDescription> apiDescriptionOrdering;
  private final Ordering<Operation> operationOrdering;
  private Set<String> produces;
  private Set<String> consumes;
  private Set<String> protocols;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final Optional<String> pathMapping;
  private boolean isUrlTemplatesEnabled;

  public DocumentationContext(DocumentationType documentationType,
      List<RequestHandler> handlerMappings,
      ApiInfo apiInfo, String groupName,
      ApiSelector apiSelector,
      Set<Class> ignorableParameterTypes,
      Map<RequestMethod, List<ResponseMessage>> globalResponseMessages,
      List<Parameter> globalOperationParameter,
      ResourceGroupingStrategy resourceGroupingStrategy,
      PathProvider pathProvider,
      List<SecurityContext> securityContexts,
      List<? extends SecurityScheme> securitySchemes,
      List<AlternateTypeRule> alternateTypeRules,
      Ordering<ApiListingReference> listingReferenceOrdering,
      Ordering<ApiDescription> apiDescriptionOrdering,
      Ordering<Operation> operationOrdering,
      Set<String> produces,
      Set<String> consumes,
      Set<String> protocols,
      GenericTypeNamingStrategy genericsNamingStrategy,
      Optional<String> pathMapping,
      boolean isUrlTemplatesEnabled) {

    this.documentationType = documentationType;
    this.handlerMappings = handlerMappings;
    this.apiInfo = apiInfo;
    this.groupName = groupName;
    this.apiSelector = apiSelector;
    this.ignorableParameterTypes = ignorableParameterTypes;
    this.globalResponseMessages = globalResponseMessages;
    this.globalOperationParameters = globalOperationParameter;
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    this.pathProvider = pathProvider;
    this.securityContexts = securityContexts;
    this.securitySchemes = securitySchemes;
    this.listingReferenceOrdering = listingReferenceOrdering;
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    this.operationOrdering = operationOrdering;
    this.produces = produces;
    this.consumes = consumes;
    this.protocols = protocols;
    this.genericsNamingStrategy = genericsNamingStrategy;
    this.pathMapping = pathMapping;
    this.isUrlTemplatesEnabled = isUrlTemplatesEnabled;
    this.alternateTypeProvider = new AlternateTypeProvider(alternateTypeRules);
  }

  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  public List<RequestHandler> getRequestHandlers() {
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
  
  public List<Parameter> getGlobalRequestParameters() {
    return globalOperationParameters;
  }

  public ResourceGroupingStrategy getResourceGroupingStrategy() {
    return resourceGroupingStrategy;
  }

  public PathProvider getPathProvider() {
    return pathProvider;
  }

  public List<SecurityContext> getSecurityContexts() {
    return securityContexts;
  }

  public List<? extends SecurityScheme> getSecuritySchemes() {
    return securitySchemes;
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

  public GenericTypeNamingStrategy getGenericsNamingStrategy() {
    return genericsNamingStrategy;
  }

  public Optional<String> getPathMapping() {
    return pathMapping;
  }

  @Incubating(value = "2.1.0")
  public boolean isUriTemplatesEnabled() {
    return isUrlTemplatesEnabled;
  }
}
