/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

import com.fasterxml.classmate.ResolvedType;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class DocumentationContext {
  private final DocumentationType documentationType;
  private final List<RequestHandler> handlerMappings;
  private final ApiInfo apiInfo;
  private final String groupName;
  private final ApiSelector apiSelector;
  private final AlternateTypeProvider alternateTypeProvider;
  private final Set<Class> ignorableParameterTypes;
  private final Map<RequestMethod, List<ResponseMessage>> globalResponseMessages;
  private final Map<HttpMethod, List<Response>> globalResponses = new HashMap<>();
  private final List<Parameter> globalOperationParameters;
  private final List<Server> servers = new ArrayList<>();
  private final List<RequestParameter> globalParameters = new ArrayList<>();
  private final PathProvider pathProvider;
  private final List<SecurityContext> securityContexts;
  private final List<SecurityScheme> securitySchemes;
  private final Comparator<ApiListingReference> listingReferenceOrdering;
  private final Comparator<ApiDescription> apiDescriptionOrdering;
  private final Comparator<Operation> operationOrdering;
  private final GenericTypeNamingStrategy genericsNamingStrategy;
  private final Optional<String> pathMapping;
  private final Set<ResolvedType> additionalModels;
  private final Set<Tag> tags;
  private final Set<String> produces;
  private final Set<String> consumes;
  private final String host;
  private final Set<String> protocols;
  private final boolean isUriTemplatesEnabled;
  private final List<VendorExtension> vendorExtensions;

  @SuppressWarnings("ParameterNumber")
  public DocumentationContext(
      DocumentationType documentationType,
      List<RequestHandler> handlerMappings,
      ApiInfo apiInfo,
      String groupName,
      ApiSelector apiSelector,
      Set<Class> ignorableParameterTypes,
      Map<RequestMethod, List<ResponseMessage>> globalResponseMessages,
      List<Parameter> globalOperationParameter,
      List<RequestParameter> globalRequestParameters,
      Map<HttpMethod, List<Response>> globalResponses,
      PathProvider pathProvider,
      List<SecurityContext> securityContexts,
      List<SecurityScheme> securitySchemes,
      List<AlternateTypeRule> alternateTypeRules,
      Comparator<ApiListingReference> listingReferenceOrdering,
      Comparator<ApiDescription> apiDescriptionOrdering,
      Comparator<Operation> operationOrdering,
      Set<String> produces,
      Set<String> consumes,
      String host,
      Set<String> protocols,
      GenericTypeNamingStrategy genericsNamingStrategy,
      Optional<String> pathMapping,
      boolean isUriTemplatesEnabled,
      Set<ResolvedType> additionalModels,
      Set<Tag> tags,
      List<VendorExtension> vendorExtensions,
      List<Server> servers) {

    this.documentationType = documentationType;
    this.handlerMappings = handlerMappings;
    this.apiInfo = apiInfo;
    this.groupName = groupName;
    this.apiSelector = apiSelector;
    this.ignorableParameterTypes = ignorableParameterTypes;
    this.globalResponseMessages = globalResponseMessages;
    this.globalOperationParameters = globalOperationParameter;
    this.servers.addAll(servers);
    this.globalParameters.addAll(globalRequestParameters);
    this.pathProvider = pathProvider;
    this.securityContexts = securityContexts;
    this.securitySchemes = securitySchemes;
    this.listingReferenceOrdering = listingReferenceOrdering;
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    this.operationOrdering = operationOrdering;
    this.produces = produces;
    this.consumes = consumes;
    this.host = host;
    this.protocols = protocols;
    this.genericsNamingStrategy = genericsNamingStrategy;
    this.pathMapping = pathMapping;
    this.isUriTemplatesEnabled = isUriTemplatesEnabled;
    this.additionalModels = additionalModels;
    this.tags = tags;
    this.alternateTypeProvider = new AlternateTypeProvider(alternateTypeRules);
    this.vendorExtensions = vendorExtensions;
    this.globalResponses.putAll(globalResponses);
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

  public Set<Class> getIgnorableParameterTypes() {
    return new HashSet<>(ignorableParameterTypes);
  }

  public Map<RequestMethod, List<ResponseMessage>> getGlobalResponseMessages() {
    return globalResponseMessages;
  }

  /**
   * @deprecated use @see {@link DocumentationContext#getGlobalParameters()} instead
   * @return list of parameters
   */
  @Deprecated
  public List<Parameter> getGlobalRequestParameters() {
    return globalOperationParameters;
  }


  public PathProvider getPathProvider() {
    return pathProvider;
  }

  public List<SecurityContext> getSecurityContexts() {
    return securityContexts;
  }

  public List<SecurityScheme> getSecuritySchemes() {
    return securitySchemes.stream()
                          .filter(Objects::nonNull)
                          .collect(Collectors.toList());
  }

  public Comparator<ApiListingReference> getListingReferenceOrdering() {
    return listingReferenceOrdering;
  }

  public Comparator<ApiDescription> getApiDescriptionOrdering() {
    return apiDescriptionOrdering;
  }

  public AlternateTypeProvider getAlternateTypeProvider() {
    return alternateTypeProvider;
  }

  public Comparator<Operation> operationOrdering() {
    return operationOrdering;
  }

  public Set<String> getProduces() {
    return produces;
  }

  public Set<String> getConsumes() {
    return consumes;
  }

  public String getHost() {
    return host;
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
    return isUriTemplatesEnabled;
  }

  public Set<ResolvedType> getAdditionalModels() {
    return additionalModels;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public List<VendorExtension> getVendorExtentions() {
    return vendorExtensions;
  }

  public Collection<RequestParameter> getGlobalParameters() {
    return globalParameters;
  }

  public Collection<Response> globalResponsesFor(HttpMethod method) {
    return globalResponses.getOrDefault(method, new ArrayList<>());
  }

  public Collection<Server> getServers() {
    return servers;
  }
}
