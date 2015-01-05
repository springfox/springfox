package com.mangofactory.swagger.plugin;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.mangofactory.documentation.plugins.DocumentationType;
import com.mangofactory.schema.GenericTypeNamingStrategy;
import com.mangofactory.schema.ResolvedTypes;
import com.mangofactory.schema.alternates.AlternateTypeProvider;
import com.mangofactory.schema.alternates.AlternateTypeRule;
import com.mangofactory.schema.alternates.WildcardType;
import com.mangofactory.service.model.ApiDescription;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.ApiListingReference;
import com.mangofactory.service.model.AuthorizationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.ApiInfoBuilder;
import com.mangofactory.springmvc.plugins.DocumentationContext;
import com.mangofactory.springmvc.plugins.DocumentationContextBuilder;
import com.mangofactory.springmvc.plugins.DocumentationPlugin;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.core.RequestMappingEvaluator;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher;
import com.mangofactory.swagger.scanners.RequestMappingPatternMatcher;
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
public class SwaggerSpringMvcPlugin implements DocumentationPlugin {

  private AtomicBoolean initialized = new AtomicBoolean(false);
  private boolean enabled = true;
  private String swaggerGroup;

  private List<String> includePatterns = newArrayList(".*?");
  private ApiInfo apiInfo;
  private SwaggerPathProvider swaggerPathProvider;
  private ResourceGroupingStrategy resourceGroupingStrategy;

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

  public SwaggerSpringMvcPlugin() {

  }

  /**
   * Sets the api's meta information as included in the json ResourceListing response.
   *
   * @param apiInfo Indicates the api information
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin apiInfo(ApiInfo apiInfo) {
    this.apiInfo = apiInfo;
    return this;
  }

  /**
   * Configures the global com.wordnik.swagger.model.AuthorizationType's applicable to all or some of the api
   * operations. The configuration of which operations have associated AuthorizationTypes is configured with
   * com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin#authorizationContext
   *
   * @param authorizationTypes a list of global AuthorizationType's
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin authorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  /**
   * Configures which api operations (via regex patterns) and HTTP methods to apply swagger authorization to.
   *
   * @param authorizationContext
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin authorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
    return this;
  }

  /**
   * If more than one instance of SwaggerSpringMvcPlugin exists, each one must have a unique swaggerGroup as
   * supplied by this method. Defaults to "default".
   *
   * @param swaggerGroup - the unique identifier of this swagger group/configuration
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin swaggerGroup(String swaggerGroup) {
    this.swaggerGroup = swaggerGroup;
    return this;
  }

  /**
   * Determines the generated, swagger specific, urls.
   *
   * By default, relative urls are generated. If absolute urls are required, supply an implementation of
   * AbsoluteSwaggerPathProvider
   *
   * @param swaggerPathProvider
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.paths.SwaggerPathProvider
   */
  public SwaggerSpringMvcPlugin pathProvider(SwaggerPathProvider swaggerPathProvider) {
    this.swaggerPathProvider = swaggerPathProvider;
    return this;
  }

  /**
   * Spring controllers or request mappings with these annotations will be excluded from the generated swagger JSON.
   *
   * @param excludeAnnotations one or more java Annotation classes
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin excludeAnnotations(Class<? extends Annotation>... excludeAnnotations) {
    this.excludeAnnotations.addAll(asList(excludeAnnotations));
    return this;
  }

  /**
   * Controls which controllers, more specifically, which Spring RequestMappings to include in the swagger Resource
   * Listing.
   *
   * Under the hood, <code>com.mangofactory.swagger.scanners.RequestMappingPatternMatcher</code>is used to match a
   * given <code>org.springframework.web.servlet.mvc.condition.PatternsRequestCondition</code> against the
   * includePatterns supplied here.
   *
   * <code>RegexRequestMappingPatternMatcher</code> is the default implementation and requires these includePatterns
   * are  valid regular expressions.
   *
   * If not supplied a single pattern ".*?" is used which matches anything and hence all RequestMappings.
   *
   * @param includePatterns - the regular expressions to determine which Spring RequestMappings to include.
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin includePatterns(String... includePatterns) {
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
   * @return this SwaggerSpringMvcPlugin
   * @see com.wordnik.swagger.annotations.ApiResponse
   * and
   * @see com.wordnik.swagger.annotations.ApiResponses
   * @see com.mangofactory.swagger.controllers.Defaults#defaultResponseMessages()
   */
  public SwaggerSpringMvcPlugin globalResponseMessage(RequestMethod requestMethod,
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
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.controllers.Defaults#defaultIgnorableParameterTypes()
   */
  public SwaggerSpringMvcPlugin ignoredParameterTypes(Class... classes) {
    this.ignorableParameterTypes.addAll(Arrays.asList(classes));
    return this;
  }

  /**
   * Overrides the default AlternateTypeProvider.
   *
   * @param alternateTypeProvider
   * @return this SwaggerSpringMvcPlugin
   */
  @Deprecated
  public SwaggerSpringMvcPlugin alternateTypeProvider(AlternateTypeProvider alternateTypeProvider) {
    return this;
  }

  /**
   * Adds model substitution rules (alternateTypeRules)
   *
   * @param alternateTypeRules
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.schema.alternates.Alternates#newRule(java.lang.reflect.Type, java.lang.reflect.Type)
   */
  public SwaggerSpringMvcPlugin alternateTypeRules(AlternateTypeRule... alternateTypeRules) {
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
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin directModelSubstitute(final Class clazz, final Class with) {
    this.substitutionRules.add(newSubstitutionFunction(clazz, with));
    return this;
  }

  private Function<TypeResolver, AlternateTypeRule> newSubstitutionFunction(final Class clazz, final Class with) {
    return new Function<TypeResolver, AlternateTypeRule>() {

      @Override
      public AlternateTypeRule apply(TypeResolver typeResolver) {
        return newRule(typeResolver.resolve(clazz), typeResolver.resolve(with));
      }
    };
  }

  /**
   * Allows ignoring predefined response message defaults
   *
   * @param apply flag to determine if the default response messages are used
   *              true   - the default response messages are added to the global response messages
   *              false  - the default response messages are added to the global response messages
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin useDefaultResponseMessages(boolean apply) {
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
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin genericModelSubstitutes(Class... genericClasses) {
    for (Class clz : genericClasses) {
      this.substitutionRules.add(newGenericSubstitutionFunction(clz));
    }
    return this;
  }

  private Function<TypeResolver, AlternateTypeRule> newGenericSubstitutionFunction(final Class clz) {
    return new Function<TypeResolver, AlternateTypeRule>() {
      @Override
      public AlternateTypeRule apply(TypeResolver typeResolver) {
        return newRule(typeResolver.resolve(clz, WildcardType.class), typeResolver.resolve(WildcardType.class));
      }
    };
  }

  /**
   * Controls how generics are encoded as swagger types, specifically around the characters used to open, close,
   * and delimit lists of types.
   *
   * @param strategy a GenericTypeNamingStrategy implementation, defaults to DefaultGenericTypeNamingStrategy
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin genericTypeNamingStrategy(GenericTypeNamingStrategy strategy) {
    ResolvedTypes.setNamingStrategy(strategy);
    return this;
  }

  /**
   * Controls how ApiListingReference's are sorted.
   * i.e the ordering of the api's within the swagger Resource Listing.
   * The default sort is Lexicographically by the ApiListingReference's path
   *
   * @param apiListingReferenceOrdering
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin apiListingReferenceOrdering(Ordering<ApiListingReference> apiListingReferenceOrdering) {
    this.apiListingReferenceOrdering = apiListingReferenceOrdering;
    return this;
  }

  /**
   * Controls how <code>com.wordnik.swagger.model.ApiDescription</code>'s are ordered.
   * The default sort is Lexicographically by the ApiDescription's path.
   *
   * @param apiDescriptionOrdering
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.scanners.ApiListingScanner
   */
  public SwaggerSpringMvcPlugin apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    return this;
  }

  /**
   * Controls which ResourceListing's, RequestMappings belong to.
   *
   * @param resourceGroupingStrategy
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.scanners.ApiListingReferenceScanner#scan(
   *com.mangofactory.springmvc.plugins.DocumentationContext)
   */
  public SwaggerSpringMvcPlugin resourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    return this;
  }

  /**
   * Hook for adding custom annotations readers. Useful when you want to add your own annotation to be mapped to swagger
   * model.
   *
   * @param requestMappingPatternMatcher an implementation of {@link com.mangofactory.swagger.scanners
   *                                     .RequestMappingPatternMatcher}. Out of the box the library comes with
   *                                     {@link com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher} and
   *                                     {@link com.mangofactory.swagger.scanners.AntRequestMappingPatternMatcher}
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin requestMappingPatternMatcher(RequestMappingPatternMatcher
                                                                     requestMappingPatternMatcher) {
    this.requestMappingPatternMatcher = requestMappingPatternMatcher;
    return this;
  }

  /**
   * Hook to externally control auto initialization of this swagger plugin instance.
   * Typically used if defer initialization.
   *
   * @param externallyConfiguredFlag - true to turn it on, false to turn it off
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin enable(boolean externallyConfiguredFlag) {
    this.enabled = externallyConfiguredFlag;
    return this;
  }


  private ApiInfo defaultApiInfo() {
    return new ApiInfoBuilder()
            .version("1.0")
            .title(this.swaggerGroup + " Title")
            .description("Api Description")
            .termsOfServiceUrl("Api terms of service")
            .contact("Contact Email")
            .license("Licence Type")
            .licenseUrl("License URL")
            .build();
  }

  /**
   * Builds the SwaggerSpringMvcPlugin by merging/overlaying user specified values.
   * It is not necessary to call this method when defined as a spring bean.
   * NOTE: Calling this method more than once has no effect.
   *
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.springmvc.plugins.DocumentationPluginsBootstrapper
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
            .withGroupName(swaggerGroup)
            .withIgnorableParameterTypes(mergedIgnorableParameterTypes)
            .withSwaggerPathProvider(swaggerPathProvider)
            .withResourceGroupingStrategy(resourceGroupingStrategy)
            .withAuthorizationContext(authorizationContext)
            .withAuthorizationTypes(authorizationTypes)
            .withApiListingReferenceOrdering(apiListingReferenceOrdering)
            .withApiDescriptionOrdering(apiDescriptionOrdering)
            .withExcludedAnnotations(mergedExcludedAnnotations)
            .build();
  }

  private void configure(Defaults defaults) {
    if (!hasText(this.swaggerGroup)) {
      this.swaggerGroup = "default";
    }

    if (null == this.apiInfo) {
      this.apiInfo = defaultApiInfo();
    }

    this.resourceGroupingStrategy
            = fromNullable(resourceGroupingStrategy).or(defaults.defaultResourceGroupingStrategy());

    this.swaggerPathProvider
            = fromNullable(swaggerPathProvider).or(defaults.defaultSwaggerPathProvider());

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

    return new Function<Function<TypeResolver,AlternateTypeRule>, AlternateTypeRule>() {
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

  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String getName() {
    return "swagger";
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true; //For now supports everything
  }
}
