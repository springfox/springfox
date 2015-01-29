package com.mangofactory.documentation.spi.service.contexts;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.mangofactory.documentation.schema.AlternateTypeRule;
import com.mangofactory.documentation.service.PathProvider;
import com.mangofactory.documentation.service.RequestMappingEvaluator;
import com.mangofactory.documentation.service.RequestMappingPatternMatcher;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiInfo;
import com.mangofactory.documentation.service.model.ApiListingReference;
import com.mangofactory.documentation.service.model.Authorization;
import com.mangofactory.documentation.service.model.AuthorizationType;
import com.mangofactory.documentation.service.model.Operation;
import com.mangofactory.documentation.service.model.ResponseMessage;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.ResourceGroupingStrategy;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.spi.service.contexts.Defaults.*;

public class DocumentationContextBuilder {
  private final Defaults defaults;

  private TypeResolver typeResolver;
  private List<RequestMappingHandlerMapping> handlerMappings;
  private ApiInfo apiInfo;
  private String groupName;
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private PathProvider pathProvider;
  private AuthorizationContext authorizationContext;
  private List<AuthorizationType> authorizationTypes;
  private Ordering<ApiListingReference> listingReferenceOrdering;
  private Ordering<ApiDescription> apiDescriptionOrdering;
  private DocumentationType documentationType;
  private RequestMappingPatternMatcher requestMappingPatternMatcher;
  private Ordering<Operation> operationOrdering;

  private Set<Class> ignorableParameterTypes = newHashSet();
  private Map<RequestMethod, List<ResponseMessage>> responseMessageOverrides = newTreeMap();
  private Set<Class<? extends Annotation>> excludeAnnotations = newHashSet();
  private Set<String> includePatterns = newHashSet();
  private boolean applyDefaultResponseMessages;
  private List<Function<TypeResolver, AlternateTypeRule>> ruleBuilders = newArrayList();
  private List<AlternateTypeRule> explicitRules = newArrayList();


  public DocumentationContextBuilder(Defaults defaults) {
    this.defaults = defaults;
    this.operationOrdering = defaults.operationOrdering();
    this.apiDescriptionOrdering = defaults.apiDescriptionOrdering();
    this.listingReferenceOrdering = defaults.apiListingReferenceOrdering();
    this.ignorableParameterTypes.addAll(defaults.defaultIgnorableParameterTypes());
  }

  public Defaults getDefaults() {
    return defaults;
  }

  public DocumentationContextBuilder withHandlerMappings(List<RequestMappingHandlerMapping> handlerMappings) {
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

  public DocumentationContextBuilder withResourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    return this;
  }

  public DocumentationContextBuilder pathProvider(PathProvider pathProvider) {
    this.pathProvider = defaultIfAbsent(pathProvider, this.pathProvider);
    return this;
  }

  public DocumentationContextBuilder authorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = defaultIfAbsent(authorizationContext, this.authorizationContext);
    return this;
  }

  public DocumentationContextBuilder authorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  public DocumentationContextBuilder apiListingReferenceOrdering(
          Ordering<ApiListingReference> listingReferenceOrdering) {

    this.listingReferenceOrdering = defaultIfAbsent(listingReferenceOrdering, this.listingReferenceOrdering);
    return this;
  }

  public DocumentationContextBuilder apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = defaultIfAbsent(apiDescriptionOrdering, this.apiDescriptionOrdering);
    return this;
  }

  public DocumentationContextBuilder withDocumentationType(DocumentationType documentationType) {
    this.documentationType = documentationType;
    return this;
  }

  public DocumentationContext build() {
    excludeAnnotations.addAll(defaults.defaultExcludeAnnotations());
    RequestMappingEvaluator requestMappingEvaluator
            = new RequestMappingEvaluator(requestMappingPatternMatcher, excludeAnnotations, includePatterns);
    Map<RequestMethod, List<ResponseMessage>> responseMessages = newTreeMap();
    if (applyDefaultResponseMessages) {
      responseMessages.putAll(defaults.defaultResponseMessages());
    }
    responseMessages.putAll(responseMessageOverrides);
    if (authorizationContext == null) {
      authorizationContext = new AuthorizationContext.AuthorizationContextBuilder()
              .withAuthorizations(new ArrayList<Authorization>())
              .withIncludePatterns(includePatterns)
              .withRequestMappingPatternMatcher(requestMappingPatternMatcher)
              .build();
    }
    return new DocumentationContext(documentationType, handlerMappings, apiInfo, groupName, requestMappingEvaluator,
            ignorableParameterTypes, responseMessages, resourceGroupingStrategy, pathProvider,
            authorizationContext, authorizationTypes, collectAlternateTypeRules(typeResolver),
            listingReferenceOrdering, apiDescriptionOrdering,
            operationOrdering);
  }

  public DocumentationContextBuilder additionalExcludedAnnotations(
          List<Class<? extends Annotation>> excludeAnnotations) {

    this.excludeAnnotations.addAll(excludeAnnotations);
    return this;
  }

  public DocumentationContextBuilder includePatterns(List<String> includePatterns) {
    this.includePatterns.addAll(includePatterns);
    return this;
  }

  public DocumentationContextBuilder requestMappingPatternMatcher(
          RequestMappingPatternMatcher requestMappingPatternMatcher) {

    this.requestMappingPatternMatcher = fromNullable(requestMappingPatternMatcher)
            .or(fromNullable(this.requestMappingPatternMatcher)).orNull();

    return this;
  }

  public DocumentationContextBuilder applyDefaultResponseMessages(boolean applyDefaultResponseMessages) {
    this.applyDefaultResponseMessages = applyDefaultResponseMessages;
    return this;
  }

  public DocumentationContextBuilder ruleBuilders(List<Function<TypeResolver, AlternateTypeRule>> ruleBuilders) {
    this.ruleBuilders.addAll(ruleBuilders);
    return this;
  }

  public DocumentationContextBuilder typeResolver(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
    return this;
  }

  public DocumentationContextBuilder operationOrdering(Ordering<Operation> operationOrdering) {
    this.operationOrdering = defaultIfAbsent(operationOrdering, this.operationOrdering);
    return this;
  }


  private List<AlternateTypeRule> collectAlternateTypeRules(TypeResolver typeResolver) {
    explicitRules.addAll(defaults.defaultRules(typeResolver));
    explicitRules.addAll(from(this.ruleBuilders)
            .transform(evaluator(typeResolver))
            .toList());
    return explicitRules;
  }

  private Function<Function<TypeResolver, AlternateTypeRule>, AlternateTypeRule>
  evaluator(final TypeResolver typeResolver) {

    return new Function<Function<TypeResolver, AlternateTypeRule>, AlternateTypeRule>() {
      @Override
      public AlternateTypeRule apply(Function<TypeResolver, AlternateTypeRule> input) {
        return input.apply(typeResolver);
      }
    };
  }


}