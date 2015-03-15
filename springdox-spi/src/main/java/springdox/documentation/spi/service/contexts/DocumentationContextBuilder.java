package springdox.documentation.spi.service.contexts;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springdox.documentation.PathProvider;
import springdox.documentation.RequestMappingEvaluator;
import springdox.documentation.RequestMappingPatternMatcher;
import springdox.documentation.builders.BuilderDefaults;
import springdox.documentation.schema.AlternateTypeRule;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.service.ApiInfo;
import springdox.documentation.service.ApiListingReference;
import springdox.documentation.service.Authorization;
import springdox.documentation.service.AuthorizationType;
import springdox.documentation.service.Operation;
import springdox.documentation.service.ResponseMessage;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.ResourceGroupingStrategy;

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

public class DocumentationContextBuilder {

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
  private RequestMappingEvaluator requestMappingEvaluator;

  private boolean applyDefaultResponseMessages;
  private Set<Class> ignorableParameterTypes = newHashSet();
  private Map<RequestMethod, List<ResponseMessage>> responseMessageOverrides = newTreeMap();
  private Set<Class<? extends Annotation>> excludeAnnotations = newHashSet();
  private Set<String> includePatterns = newHashSet();
  private List<AlternateTypeRule> rules = newArrayList();
  private Map<RequestMethod, List<ResponseMessage>> defaultResponseMessages = newHashMap();
  private Set<String> protocols = newHashSet();
  private Set<String> produces = newHashSet();
  private Set<String> consumes = newHashSet();

  public DocumentationContextBuilder(DocumentationType documentationType) {
    this.documentationType = documentationType;
  }

  public DocumentationContextBuilder handlerMappings(List<RequestMappingHandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
    return this;
  }

  public DocumentationContextBuilder apiInfo(ApiInfo apiInfo) {
    this.apiInfo = BuilderDefaults.defaultIfAbsent(apiInfo, this.apiInfo);
    return this;
  }

  public DocumentationContextBuilder groupName(String groupName) {
    this.groupName = BuilderDefaults.defaultIfAbsent(groupName, this.groupName);
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
    this.pathProvider = BuilderDefaults.defaultIfAbsent(pathProvider, this.pathProvider);
    return this;
  }

  public DocumentationContextBuilder authorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = BuilderDefaults.defaultIfAbsent(authorizationContext, this.authorizationContext);
    return this;
  }

  public DocumentationContextBuilder authorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  public DocumentationContextBuilder apiListingReferenceOrdering(
          Ordering<ApiListingReference> listingReferenceOrdering) {

    this.listingReferenceOrdering = BuilderDefaults.defaultIfAbsent(listingReferenceOrdering, this
            .listingReferenceOrdering);
    return this;
  }

  public DocumentationContextBuilder apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = BuilderDefaults.defaultIfAbsent(apiDescriptionOrdering, this.apiDescriptionOrdering);
    return this;
  }

  public DocumentationContextBuilder requestMappingEvaluator(RequestMappingEvaluator requestMappingEvaluator) {
    this.requestMappingEvaluator = requestMappingEvaluator;
    return this;
  }

  private Map<RequestMethod, List<ResponseMessage>> aggregateResponseMessages() {
    Map<RequestMethod, List<ResponseMessage>> responseMessages = newHashMap();
    if (applyDefaultResponseMessages) {
      responseMessages.putAll(defaultResponseMessages);
    }
    responseMessages.putAll(responseMessageOverrides);
    return responseMessages;
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
    rules.addAll(from(ruleBuilders)
            .transform(evaluator(typeResolver))
            .toList());
    return this;
  }

  public DocumentationContextBuilder typeResolver(TypeResolver typeResolver) {
    this.typeResolver = typeResolver;
    return this;
  }

  public DocumentationContextBuilder operationOrdering(Ordering<Operation> operationOrdering) {
    this.operationOrdering = BuilderDefaults.defaultIfAbsent(operationOrdering, this.operationOrdering);
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
  public DocumentationContextBuilder protocols(Set<String> protocols) {
    this.protocols.addAll(protocols);
    return this;
  }
  public DocumentationContext build() {
    requestMappingEvaluator.appendExcludeAnnotations(excludeAnnotations);
    requestMappingEvaluator.appendIncludePatterns(includePatterns);
    Map<RequestMethod, List<ResponseMessage>> responseMessages = aggregateResponseMessages();
    AuthorizationContext authorizationContext = fromNullable(this.authorizationContext)
            .or(new AuthorizationContext.AuthorizationContextBuilder()
                    .withAuthorizations(new ArrayList<Authorization>())
                    .withIncludePatterns(includePatterns)
                    .withRequestMappingPatternMatcher(requestMappingPatternMatcher)
                    .build());
    return new DocumentationContext(documentationType, handlerMappings, apiInfo, groupName,
            requestMappingEvaluator, ignorableParameterTypes, responseMessages,
            resourceGroupingStrategy, pathProvider,
            authorizationContext, authorizationTypes, rules,
            listingReferenceOrdering, apiDescriptionOrdering,
            operationOrdering, produces, consumes, protocols);
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