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
import com.fasterxml.classmate.TypeResolver;
import org.springframework.core.OrderComparator;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.service.Tags;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.service.ResourceGroupingStrategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

public class DocumentationContextBuilder {

  private final List<SecurityContext> securityContexts = new ArrayList<>();
  private final Set<Class> ignorableParameterTypes = new HashSet<>();
  private final Map<RequestMethod, List<ResponseMessage>> responseMessageOverrides = new TreeMap<>();
  private final List<Parameter> globalOperationParameters = new ArrayList<>();
  private final List<AlternateTypeRule> rules = new ArrayList<>();
  private final Map<RequestMethod, List<ResponseMessage>> defaultResponseMessages = new HashMap<>();
  private final Set<String> protocols = new HashSet<>();
  private final Set<String> produces = new LinkedHashSet<>();
  private final Set<String> consumes = new LinkedHashSet<>();
  private final Set<ResolvedType> additionalModels = new HashSet<>();
  private final Set<Tag> tags = new TreeSet<>(Tags.tagComparator());
  private List<VendorExtension> vendorExtensions = new ArrayList<VendorExtension>();

  private TypeResolver typeResolver;
  private List<RequestHandler> handlerMappings;
  private ApiInfo apiInfo;
  private String groupName;
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private PathProvider pathProvider;
  private List<? extends SecurityScheme> securitySchemes;
  private Comparator<ApiListingReference> listingReferenceOrdering;
  private Comparator<ApiDescription> apiDescriptionOrdering;
  private DocumentationType documentationType;
  private Comparator<Operation> operationOrdering;
  private boolean applyDefaultResponseMessages;
  private ApiSelector apiSelector = ApiSelector.DEFAULT;
  private String host;
  private GenericTypeNamingStrategy genericsNamingStrategy;
  private Optional<String> pathMapping;
  private boolean isUrlTemplatesEnabled;

  public DocumentationContextBuilder(DocumentationType documentationType) {
    this.documentationType = documentationType;
  }

  public DocumentationContextBuilder requestHandlers(List<RequestHandler> handlerMappings) {
    this.handlerMappings = handlerMappings;
    return this;
  }

  public DocumentationContextBuilder apiInfo(ApiInfo apiInfo) {
    this.apiInfo = defaultIfAbsent(apiInfo, this.apiInfo);
    return this;
  }

  public DocumentationContextBuilder groupName(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  public DocumentationContextBuilder additionalIgnorableTypes(Set<Class> ignorableParameterTypes) {
    this.ignorableParameterTypes.addAll(ignorableParameterTypes);
    return this;
  }

  public DocumentationContextBuilder additionalResponseMessages(
      Map<RequestMethod, List<ResponseMessage>> additionalResponseMessages) {
    this.responseMessageOverrides.putAll(additionalResponseMessages);
    return this;
  }
  
  public DocumentationContextBuilder additionalOperationParameters(List<Parameter> globalRequestParameters) {
    this.globalOperationParameters.addAll(nullToEmptyList(globalRequestParameters));
    return this;
  }

  /**
   * @deprecated  @since 2.2.0 - only here for backward compatibility
   * @param resourceGroupingStrategy - custom resource grouping strategy
   * @return this
   */
  @Deprecated
  public DocumentationContextBuilder withResourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    return this;
  }

  public DocumentationContextBuilder pathProvider(PathProvider pathProvider) {
    this.pathProvider = defaultIfAbsent(pathProvider, this.pathProvider);
    return this;
  }

  public DocumentationContextBuilder securityContexts(List<SecurityContext> securityContext) {
    this.securityContexts.addAll(nullToEmptyList(securityContext));
    return this;
  }

  public DocumentationContextBuilder securitySchemes(List<? extends SecurityScheme> securitySchemes) {
    this.securitySchemes = securitySchemes;
    return this;
  }

  public DocumentationContextBuilder apiListingReferenceOrdering(
          Comparator<ApiListingReference> listingReferenceOrdering) {

    this.listingReferenceOrdering = defaultIfAbsent(listingReferenceOrdering, this.listingReferenceOrdering);
    return this;
  }

  public DocumentationContextBuilder apiDescriptionOrdering(Comparator<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = defaultIfAbsent(apiDescriptionOrdering, this.apiDescriptionOrdering);
    return this;
  }

  private Map<RequestMethod, List<ResponseMessage>> aggregateResponseMessages() {
    Map<RequestMethod, List<ResponseMessage>> responseMessages = new HashMap<>();
    if (applyDefaultResponseMessages) {
      responseMessages.putAll(defaultResponseMessages);
    }
    responseMessages.putAll(responseMessageOverrides);
    return responseMessages;
  }

  public DocumentationContextBuilder applyDefaultResponseMessages(boolean applyDefaultResponseMessages) {
    this.applyDefaultResponseMessages = applyDefaultResponseMessages;
    return this;
  }

  public DocumentationContextBuilder ruleBuilders(List<Function<TypeResolver, AlternateTypeRule>> ruleBuilders) {
    rules.addAll(ruleBuilders.stream()
        .map(evaluator(typeResolver))
        .collect(toList()));
    return this;
  }

  public DocumentationContextBuilder typeResolver(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
    return this;
  }

  public DocumentationContextBuilder operationOrdering(Comparator<Operation> operationOrdering) {
    this.operationOrdering = defaultIfAbsent(operationOrdering, this.operationOrdering);
    return this;
  }

  public DocumentationContextBuilder rules(List<AlternateTypeRule> rules) {
    this.rules.addAll(rules);
    return this;
  }

  public DocumentationContextBuilder defaultResponseMessages(
      Map<RequestMethod, List<ResponseMessage>> defaultResponseMessages) {
    this.defaultResponseMessages.putAll(defaultResponseMessages);
    return this;
  }

  public DocumentationContextBuilder produces(Set<String> produces) {
    this.produces.addAll(produces);
    return this;
  }

  public DocumentationContextBuilder consumes(Set<String> consumes) {
    this.consumes.addAll(consumes);
    return this;
  }

  public DocumentationContextBuilder genericsNaming(GenericTypeNamingStrategy genericsNamingStrategy) {
    this.genericsNamingStrategy = genericsNamingStrategy;
    return this;
  }

  public DocumentationContextBuilder host(String host) {
    this.host = defaultIfAbsent(host, this.host);
    return this;
  }

  public DocumentationContextBuilder protocols(Set<String> protocols) {
    this.protocols.addAll(protocols);
    return this;
  }

  public DocumentationContextBuilder selector(ApiSelector apiSelector) {
    this.apiSelector = apiSelector;
    return this;
  }

  public DocumentationContextBuilder pathMapping(Optional<String> pathMapping) {
    this.pathMapping = pathMapping;
    return this;
  }

  public DocumentationContextBuilder enableUrlTemplating(boolean isUrlTemplatesEnabled) {
    this.isUrlTemplatesEnabled = isUrlTemplatesEnabled;
    return this;
  }

  public DocumentationContextBuilder additionalModels(Set<ResolvedType> additionalModels) {
    this.additionalModels.addAll(additionalModels);
    return this;
  }

  public DocumentationContextBuilder tags(Set<Tag> tags) {
    this.tags.addAll(tags);
    return this;
  }

  public DocumentationContextBuilder vendorExtentions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public DocumentationContext build() {
    Map<RequestMethod, List<ResponseMessage>> responseMessages = aggregateResponseMessages();
    OrderComparator.sort(rules);
    return new DocumentationContext(documentationType,
        handlerMappings,
        apiInfo,
        groupName,
        apiSelector,
        ignorableParameterTypes,
        responseMessages,
        globalOperationParameters,
        resourceGroupingStrategy,
        pathProvider,
        securityContexts,
        securitySchemes,
        rules,
        listingReferenceOrdering,
        apiDescriptionOrdering,
        operationOrdering,
        produces,
        consumes,
        host,
        protocols,
        genericsNamingStrategy,
        pathMapping,
        isUrlTemplatesEnabled,
        additionalModels,
        tags,
        vendorExtensions);
  }

  private Function<Function<TypeResolver, AlternateTypeRule>, AlternateTypeRule>
      evaluator(final TypeResolver typeResolver) {

    return input -> input.apply(typeResolver);
  }
}
