package com.mangofactory.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.mangofactory.schema.GenericTypeNamingStrategy;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.alternates.AlternateTypeRule;
import com.mangofactory.schema.alternates.WildcardType;
import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.ApiInfoBuilder;
import com.mangofactory.spring.web.PathProvider;
import com.mangofactory.spring.web.RequestMappingEvaluator;
import com.mangofactory.spring.web.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.spring.web.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.spring.web.scanners.RegexRequestMappingPatternMatcher;
import com.mangofactory.spring.web.scanners.RequestMappingPatternMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.schema.alternates.Alternates.*;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.*;

/**
 * A builder which is intended to be the primary interface into the swagger-springmvc framework.
 * Provides sensible defaults and convenience methods for configuration.
 */
public class DocumentationConfigurer implements DocumentationPlugin {

  private final DocumentationType documentationType;
  private AtomicBoolean initialized = new AtomicBoolean(false);
  private boolean enabled = true;
  private String groupName;

  private List<String> includePatterns = newArrayList(".*?");
  private ApiInfo apiInfo;
  private PathProvider pathProvider;

  private List<Class<? extends Annotation>> excludeAnnotations = newArrayList();
  private List<Class<? extends Annotation>> mergedExcludedAnnotations = newArrayList();

  private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages = newHashMap();
  private Map<RequestMethod, List<ResponseMessage>> mergedResponseMessages;

  private Set<Class> ignorableParameterTypes = newHashSet();
  private Set<Class> mergedIgnorableParameterTypes;

  private boolean applyDefaultResponseMessages = true;
  private RequestMappingEvaluator requestMappingEvaluator;
  private RequestMappingPatternMatcher requestMappingPatternMatcher = new RegexRequestMappingPatternMatcher();

  private List<Function<TypeResolver, AlternateTypeRule>> substitutionRules = newArrayList();
  private List<AlternateTypeRule> alternateTypeRules = newArrayList();

  private List<AuthorizationType> authorizationTypes;
  private AuthorizationContext authorizationContext = AuthorizationContext.builder().build();
  private Ordering<ApiListingReference> apiListingReferenceOrdering = new ResourceListingLexicographicalOrdering();
  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();

  public DocumentationConfigurer(DocumentationType documentationType) {
    this.documentationType = documentationType;
  }

  /**
   * Sets the api's meta information as included in the json ResourceListing response.
   *
   * @param apiInfo Indicates the api information
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer apiInfo(ApiInfo apiInfo) {
    this.apiInfo = apiInfo;
    return this;
  }

  /**
   * Configures the global com.wordnik.swagger.model.AuthorizationType's applicable to all or some of the api
   * operations. The configuration of which operations have associated AuthorizationTypes is configured with
   * com.mangofactory.swagger.plugins.DocumentationConfigurer#authorizationContext
   *
   * @param authorizationTypes a list of global AuthorizationType's
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer authorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  /**
   * Configures which api operations (via regex patterns) and HTTP methods to apply swagger authorization to.
   *
   * @param authorizationContext
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer authorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
    return this;
  }

  /**
   * If more than one instance of DocumentationConfigurer exists, each one must have a unique groupName as
   * supplied by this method. Defaults to "default".
   *
   * @param groupName - the unique identifier of this swagger group/configuration
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer groupName(String groupName) {
    this.groupName = groupName;
    return this;
  }

  /**
   * Determines the generated, swagger specific, urls.
   *
   * By default, relative urls are generated. If absolute urls are required, supply an implementation of
   * AbsoluteSwaggerPathProvider
   *
   * @param pathProvider
   * @return this DocumentationConfigurer
   * @see com.mangofactory.spring.web.PathProvider
   */
  public DocumentationConfigurer pathProvider(PathProvider pathProvider) {
    this.pathProvider = pathProvider;
    return this;
  }

  /**
   * Spring controllers or request mappings with these annotations will be excluded from the generated swagger JSON.
   *
   * @param excludeAnnotations one or more java Annotation classes
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer excludeAnnotations(Class<? extends Annotation>... excludeAnnotations) {
    this.excludeAnnotations.addAll(asList(excludeAnnotations));
    return this;
  }

  /**
   * Controls which controllers, more specifically, which Spring RequestMappings to include in the swagger Resource
   * Listing.
   *
   * Under the hood, <code>com.mangofactory.spring.web.scanners.RequestMappingPatternMatcher</code>is used to match a
   * given <code>org.springframework.web.servlet.mvc.condition.PatternsRequestCondition</code> against the
   * includePatterns supplied here.
   *
   * <code>RegexRequestMappingPatternMatcher</code> is the default implementation and requires these includePatterns
   * are  valid regular expressions.
   *
   * If not supplied a single pattern ".*?" is used which matches anything and hence all RequestMappings.
   *
   * @param includePatterns - the regular expressions to determine which Spring RequestMappings to include.
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer includePatterns(String... includePatterns) {
    this.includePatterns = asList(includePatterns);
    return this;
  }

  /**
   * Overrides the default http response messages at the http request method level.
   *
   * To set specific response messages for specific api operations use the swagger core annotations on
   * the appropriate controller methods.
   *
   * @param requestMethod    - http request method for which to apply the message
   * @param responseMessages - the message
   * @return this DocumentationConfigurer
   * @see com.wordnik.swagger.annotations.ApiResponse
   * and
   * @see com.wordnik.swagger.annotations.ApiResponses
   * @see Defaults#defaultResponseMessages()
   */
  public DocumentationConfigurer globalResponseMessage(RequestMethod requestMethod,
                                                       List<ResponseMessage> responseMessages) {

    this.globalResponseMessages.put(requestMethod, responseMessages);
    return this;
  }

  /**
   * Adds ignored controller method parameter types so that the framework does not generate swagger model or parameter
   * information for these specific types.
   * e.g. HttpServletRequest/HttpServletResponse which are already included in the pre-configured ignored types.
   *
   * @param classes the classes to ignore
   * @return this DocumentationConfigurer
   * @see Defaults#defaultIgnorableParameterTypes()
   */
  public DocumentationConfigurer ignoredParameterTypes(Class... classes) {
    this.ignorableParameterTypes.addAll(Arrays.asList(classes));
    return this;
  }

  /**
   * Overrides the default AlternateTypeProvider.
   *
   * @param alternateTypeProvider
   * @return this DocumentationConfigurer
   */
  @Deprecated
  public DocumentationConfigurer alternateTypeProvider(AlternateTypeProvider alternateTypeProvider) {
    return this;
  }

  /**
   * Adds model substitution rules (alternateTypeRules)
   *
   * @param alternateTypeRules
   * @return this DocumentationConfigurer
   * @see com.mangofactory.schema.alternates.Alternates#newRule(java.lang.reflect.Type, java.lang.reflect.Type)
   */
  public DocumentationConfigurer alternateTypeRules(AlternateTypeRule... alternateTypeRules) {
    this.alternateTypeRules.addAll(Arrays.asList(alternateTypeRules));
    return this;
  }

  /**
   * Directly substitutes a model class with the supplied substitute
   * e.g
   * <code>directModelSubstitute(LocalDate.class, Date.class)</code>
   * would substitute LocalDate with Date
   *
   * @param clazz class to substitute
   * @param with  the class which substitutes 'clazz'
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer directModelSubstitute(final Class clazz, final Class with) {
    this.substitutionRules.add(newSubstitutionFunction(clazz, with));
    return this;
  }

  /**
   * Allows ignoring predefined response message defaults
   *
   * @param apply flag to determine if the default response messages are used
   *              true   - the default response messages are added to the global response messages
   *              false  - the default response messages are added to the global response messages
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer useDefaultResponseMessages(boolean apply) {
    this.applyDefaultResponseMessages = apply;
    return this;
  }

  /**
   * Substitutes each generic class with it's direct parameterized type.
   * e.g.
   * <code>.genericModelSubstitutes(ResponseEntity.class)</code>
   * would substitute ResponseEntity &lt;MyModel&gt; with MyModel
   *
   * @param genericClasses - generic classes on which to apply generic model substitution.
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer genericModelSubstitutes(Class... genericClasses) {
    for (Class clz : genericClasses) {
      this.substitutionRules.add(newGenericSubstitutionFunction(clz));
    }
    return this;
  }

  /**
   * Controls how generics are encoded as swagger types, specifically around the characters used to open, close,
   * and delimit lists of types.
   *
   * @param strategy a GenericTypeNamingStrategy implementation, defaults to DefaultGenericTypeNamingStrategy
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer genericTypeNamingStrategy(GenericTypeNamingStrategy strategy) {
    ResolvedTypes.setNamingStrategy(strategy);
    return this;
  }

  /**
   * Controls how ApiListingReference's are sorted.
   * i.e the ordering of the api's within the swagger Resource Listing.
   * The default sort is Lexicographically by the ApiListingReference's path
   *
   * @param apiListingReferenceOrdering
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer apiListingReferenceOrdering(Ordering<ApiListingReference>
                                                                     apiListingReferenceOrdering) {
    this.apiListingReferenceOrdering = apiListingReferenceOrdering;
    return this;
  }

  /**
   * Controls how <code>com.wordnik.swagger.model.ApiDescription</code>'s are ordered.
   * The default sort is Lexicographically by the ApiDescription's path.
   *
   * @param apiDescriptionOrdering
   * @return this DocumentationConfigurer
   * @see com.mangofactory.spring.web.scanners.ApiListingScanner
   */
  public DocumentationConfigurer apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    return this;
  }

  /**
   * Hook for adding custom annotations readers. Useful when you want to add your own annotation to be mapped to swagger
   * model.
   *
   * @param requestMappingPatternMatcher an implementation of {@link com.mangofactory.spring.web.scanners
   *                                     .RequestMappingPatternMatcher}. Out of the box the library comes with
   *                                     {@link com.mangofactory.spring.web.scanners
   *                                     .RegexRequestMappingPatternMatcher} and
   *                                     {@link com.mangofactory.spring.web.scanners.AntRequestMappingPatternMatcher}
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer requestMappingPatternMatcher(RequestMappingPatternMatcher
                                                                      requestMappingPatternMatcher) {
    this.requestMappingPatternMatcher = requestMappingPatternMatcher;
    return this;
  }

  /**
   * Hook to externally control auto initialization of this swagger plugin instance.
   * Typically used if defer initialization.
   *
   * @param externallyConfiguredFlag - true to turn it on, false to turn it off
   * @return this DocumentationConfigurer
   */
  public DocumentationConfigurer enable(boolean externallyConfiguredFlag) {
    this.enabled = externallyConfiguredFlag;
    return this;
  }

  /**
   * Builds the DocumentationConfigurer by merging/overlaying user specified values.
   * It is not necessary to call this method when defined as a spring bean.
   * NOTE: Calling this method more than once has no effect.
   *
   * @return this DocumentationConfigurer
   * @see com.mangofactory.spring.web.plugins.DocumentationPluginsBootstrapper
   */
  public DocumentationContext build(DocumentationContextBuilder builder) {
    if (initialized.compareAndSet(false, true)) {
      configure(builder.getDefaults());
      buildScannerContext(builder.getDefaults());
    }
    return builder
            .withApiInfo(apiInfo)
            .withGlobalResponseMessages(mergedResponseMessages)
            .withRequestMappingEvaluator(requestMappingEvaluator)
            .withGroupName(groupName)
            .withIgnorableParameterTypes(mergedIgnorableParameterTypes)
            .withPathProvider(pathProvider)
            .withAuthorizationContext(authorizationContext)
            .withAuthorizationTypes(authorizationTypes)
            .withApiListingReferenceOrdering(apiListingReferenceOrdering)
            .withApiDescriptionOrdering(apiDescriptionOrdering)
            .build();
  }

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public DocumentationType getDocumentationType() {
    return documentationType;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true; //For now supports everything
  }

  private Function<TypeResolver, AlternateTypeRule> newSubstitutionFunction(final Class clazz, final Class with) {
    return new Function<TypeResolver, AlternateTypeRule>() {

      @Override
      public AlternateTypeRule apply(TypeResolver typeResolver) {
        return newRule(typeResolver.resolve(clazz), typeResolver.resolve(with));
      }
    };
  }

  private Function<TypeResolver, AlternateTypeRule> newGenericSubstitutionFunction(final Class clz) {
    return new Function<TypeResolver, AlternateTypeRule>() {
      @Override
      public AlternateTypeRule apply(TypeResolver typeResolver) {
        return newRule(typeResolver.resolve(clz, WildcardType.class), typeResolver.resolve(WildcardType.class));
      }
    };
  }

  private ApiInfo defaultApiInfo() {
    return new ApiInfoBuilder()
            .version("1.0")
            .title(this.groupName + " Title")
            .description("Api Description")
            .termsOfServiceUrl("Api terms of service")
            .contact("Contact Email")
            .license("Licence Type")
            .licenseUrl("License URL")
            .build();
  }

  private void configure(Defaults defaults) {
    if (!hasText(this.groupName)) {
      this.groupName = "default";
    }

    if (null == this.apiInfo) {
      this.apiInfo = defaultApiInfo();
    }

    this.pathProvider
            = fromNullable(pathProvider).or(defaults.defaultPathProvider());

    mergedExcludedAnnotations.addAll(defaults.defaultExcludeAnnotations());
    mergedExcludedAnnotations.addAll(this.excludeAnnotations);

    requestMappingEvaluator
            = new RequestMappingEvaluator(mergedExcludedAnnotations, requestMappingPatternMatcher, includePatterns);
  }

  private void buildScannerContext(final Defaults defaults) {
    mergedResponseMessages = newTreeMap();
    if (this.applyDefaultResponseMessages) {
      mergedResponseMessages.putAll(defaults.defaultResponseMessages());
    }
    mergedResponseMessages.putAll(this.globalResponseMessages);

    mergedIgnorableParameterTypes = newHashSet();
    mergedIgnorableParameterTypes.addAll(defaults.defaultIgnorableParameterTypes());
    mergedIgnorableParameterTypes.addAll(this.ignorableParameterTypes);

    TypeResolver typeResolver = defaults.getTypeResolver();
    for (AlternateTypeRule rule : collectAlternateTypeRules(typeResolver)) {
      defaults.getAlternateTypeProvider().addRule(rule);
    }
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

  private List<AlternateTypeRule> collectAlternateTypeRules(TypeResolver typeResolver) {
    alternateTypeRules.addAll(FluentIterable
            .from(this.substitutionRules)
            .transform(evaluator(typeResolver))
            .toList());

    return alternateTypeRules;
  }
}
