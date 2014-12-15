package com.mangofactory.swagger.plugin;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Optional;
import com.google.common.collect.Ordering;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.RequestMappingEvaluator;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.mangofactory.swagger.models.GenericTypeNamingStrategy;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.models.ResolvedTypes;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.alternates.AlternateTypeRule;
import com.mangofactory.swagger.models.alternates.WildcardType;
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.readers.operation.RequestMappingReader;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher;
import com.mangofactory.swagger.scanners.RequestMappingPatternMatcher;
import com.mangofactory.swagger.models.dto.ApiDescription;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.ApiListingReference;
import com.mangofactory.swagger.models.dto.AuthorizationType;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.mangofactory.swagger.models.alternates.Alternates.*;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.*;

/**
 * A builder which is intended to be the primary interface into the swagger-springmvc framework.
 * Provides sensible defaults and convenience methods for configuration.
 */
public class SwaggerSpringMvcPlugin {

  private ModelProvider modelProvider;
  private String swaggerGroup;
  private List<String> includePatterns = newArrayList(".*?");
  private SwaggerPathProvider swaggerPathProvider;
  private List<AuthorizationType> authorizationTypes;
  private ApiInfo apiInfo;
  private AuthorizationContext authorizationContext;
  private List<Class<? extends Annotation>> excludeAnnotations = new ArrayList<Class<? extends Annotation>>();
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private String apiVersion = "1.0";

  private SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
  private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages = newHashMap();
  private Set<Class> ignorableParameterTypes = new HashSet<Class>();
  private AlternateTypeProvider alternateTypeProvider;
  private List<AlternateTypeRule> alternateTypeRules = new ArrayList<AlternateTypeRule>();
  private SpringSwaggerConfig springSwaggerConfig;
  private SwaggerApiResourceListing swaggerApiResourceListing;
  private Ordering<ApiListingReference> apiListingReferenceOrdering = new ResourceListingLexicographicalOrdering();
  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private ApiListingReferenceScanner apiListingReferenceScanner;
  private AtomicBoolean initialized = new AtomicBoolean(false);
  private Collection<RequestMappingReader> customAnnotationReaders = newArrayList();
  private boolean applyDefaultResponseMessages = true;
  private RequestMappingEvaluator requestMappingEvaluator;
  private RequestMappingPatternMatcher requestMappingPatternMatcher = new RegexRequestMappingPatternMatcher();
  private boolean enabled = true;

  /**
   * Default constructor.
   * The argument springSwaggerConfig is used to by this class to establish sensible defaults.
   *
   * @param springSwaggerConfig
   */
  public SwaggerSpringMvcPlugin(SpringSwaggerConfig springSwaggerConfig) {
    Assert.notNull(springSwaggerConfig);
    this.springSwaggerConfig = springSwaggerConfig;
  }


  /**
   * Sets the api's meta information as included in the json ResourceListing response.
   *
   * @param apiInfo
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
   * @see com.mangofactory.swagger.configuration.SpringSwaggerConfig#defaultResponseMessages()
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
   * @see com.mangofactory.swagger.configuration.SpringSwaggerConfig#defaultIgnorableParameterTypes()
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
  public SwaggerSpringMvcPlugin alternateTypeProvider(AlternateTypeProvider alternateTypeProvider) {
    this.alternateTypeProvider = alternateTypeProvider;
    return this;
  }

  /**
   * Sets the api version. The 'apiVersion' on the swagger Resource Listing
   *
   * @param apiVersion
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin apiVersion(String apiVersion) {
    Assert.hasText(apiVersion, "apiVersion must contain text");
    this.apiVersion = apiVersion;
    return this;
  }

  /**
   * Overrides the default <code>com.mangofactory.swagger.models.ModelProvider</code>
   *
   * @param modelProvider
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin modelProvider(ModelProvider modelProvider) {
    this.modelProvider = modelProvider;
    return this;
  }

  /**
   * Adds model substitution rules (alternateTypeRules)
   *
   * @param alternateTypeRules
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.models.alternates.Alternates#newRule(java.lang.reflect.Type, java.lang.reflect.Type)
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
  public SwaggerSpringMvcPlugin directModelSubstitute(Class clazz, Class with) {
    TypeResolver typeResolver = swaggerGlobalSettings.getTypeResolver();
    this.alternateTypeRules.add(newRule(typeResolver.resolve(clazz), typeResolver.resolve(with)));
    return this;
  }

  /**
   * Allows ignoring predefined response message defaults
   * @param apply flag to determine if the default response messages are used
   *     true   - the default response messages are added to the global response messages
   *     false  - the default response messages are added to the global response messages
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
    TypeResolver typeResolver = swaggerGlobalSettings.getTypeResolver();
    for (Class clz : genericClasses) {
      this.alternateTypeRules.add(newRule(typeResolver.resolve(clz, WildcardType.class),
              typeResolver.resolve(WildcardType.class)));
    }
    return this;
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
   * @see com.mangofactory.swagger.scanners.ApiListingReferenceScanner#scanSpringRequestMappings()
   */
  public SwaggerSpringMvcPlugin resourceGroupingStrategy(ResourceGroupingStrategy resourceGroupingStrategy) {
    this.resourceGroupingStrategy = resourceGroupingStrategy;
    return this;
  }

  /**
   * Hook for adding custom annotations readers. Useful when you want to add your own annotation to be mapped to swagger
   * model.
   *
   * @param customAnnotationReaders list of {@link com.mangofactory.swagger.readers.operation.RequestMappingReader}
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin customAnnotationReaders(Collection<RequestMappingReader> customAnnotationReaders) {
    this.customAnnotationReaders = newArrayList(customAnnotationReaders);
    return this;
  }

  /**
   * Hook for adding custom annotations readers. Useful when you want to add your own annotation to be mapped to swagger
   * model.
   *
   * @param requestMappingPatternMatcher an implementation of {@link com.mangofactory.swagger.scanners
   * .RequestMappingPatternMatcher}. Out of the box the library comes with
   * {@link com.mangofactory.swagger.scanners.RegexRequestMappingPatternMatcher} and
   * {@link com.mangofactory.swagger.scanners.AntRequestMappingPatternMatcher}
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin requestMappingPatternMatcher(RequestMappingPatternMatcher requestMappingPatternMatcher) {
    this.requestMappingPatternMatcher = requestMappingPatternMatcher;
    return this;
  }

  /**
   * Hook to externally control the fact the swagger processing engine
   * @param externallyConfiguredFlag - true to turn it on, false to turn it off
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin enable(boolean externallyConfiguredFlag) {
    this.enabled = externallyConfiguredFlag;
    return this;
  }

  private ApiInfo defaultApiInfo() {
    return new ApiInfo(
            this.swaggerGroup + " Title",
            "Api Description",
            "Api terms of service",
            "Contact Email",
            "Licence Type",
            "License URL"
    );
  }

  /**
   * Called by the framework hence protected
   */
  protected void initialize() {
    if (enabled) {
      this.build().swaggerApiResourceListing.initialize();
    }
  }

  /**
   * Builds the SwaggerSpringMvcPlugin by merging/overlaying user specified values.
   * It is not necessary to call this method when defined as a spring bean.
   * NOTE: Calling this method more than once has no effect.
   *
   * @return this SwaggerSpringMvcPlugin
   * @see com.mangofactory.swagger.plugin.SwaggerPluginAdapter
   */
  public SwaggerSpringMvcPlugin build() {
    if (initialized.compareAndSet(false, true)) {
      configure();
      buildSwaggerGlobalSettings();
      buildApiListingReferenceScanner();
      buildSwaggerApiResourceListing();
    }
    return this;
  }

  private void configure() {
    if (!hasText(this.swaggerGroup)) {
      this.swaggerGroup = "default";
    }

    if (null == this.apiInfo) {
      this.apiInfo = defaultApiInfo();
    }

    this.resourceGroupingStrategy = Optional.fromNullable(resourceGroupingStrategy)
            .or(springSwaggerConfig.defaultResourceGroupingStrategy());

    this.swaggerPathProvider = Optional.fromNullable(swaggerPathProvider)
            .or(springSwaggerConfig.defaultSwaggerPathProvider());

    this.alternateTypeProvider = Optional.fromNullable(alternateTypeProvider)
            .or(springSwaggerConfig.defaultAlternateTypeProvider());

    this.modelProvider = Optional.fromNullable(modelProvider).or(springSwaggerConfig.defaultModelProvider());

    List<Class<? extends Annotation>> mergedExcludedAnnotations = springSwaggerConfig.defaultExcludeAnnotations();
    mergedExcludedAnnotations.addAll(this.excludeAnnotations);
    requestMappingEvaluator
            = new RequestMappingEvaluator(mergedExcludedAnnotations, requestMappingPatternMatcher, includePatterns);
  }

  private void buildSwaggerGlobalSettings() {
    Map<RequestMethod, List<ResponseMessage>> mergedResponseMessages = newHashMap();
    if (this.applyDefaultResponseMessages) {
      mergedResponseMessages.putAll(springSwaggerConfig.defaultResponseMessages());
    }
    mergedResponseMessages.putAll(this.globalResponseMessages);
    swaggerGlobalSettings.setGlobalResponseMessages(mergedResponseMessages);

    Set<Class> mergedIgnorableParameterTypes = new HashSet<Class>();
    mergedIgnorableParameterTypes.addAll(springSwaggerConfig.defaultIgnorableParameterTypes());
    mergedIgnorableParameterTypes.addAll(this.ignorableParameterTypes);
    swaggerGlobalSettings.setIgnorableParameterTypes(mergedIgnorableParameterTypes);
    addSpringMvcPassthroughTypes();

    for (AlternateTypeRule rule : this.alternateTypeRules) {
      this.alternateTypeProvider.addRule(rule);
    }
    swaggerGlobalSettings.setAlternateTypeProvider(this.alternateTypeProvider);
  }

  private void addSpringMvcPassthroughTypes() {
    TypeResolver typeResolver = swaggerGlobalSettings.getTypeResolver();
    alternateTypeProvider.addRule(newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));

    alternateTypeProvider.addRule(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
            typeResolver.resolve(WildcardType.class)));
  }

  private void buildSwaggerApiResourceListing() {
    swaggerApiResourceListing = new SwaggerApiResourceListing(springSwaggerConfig.swaggerCache(), this.swaggerGroup);
    swaggerApiResourceListing.setSwaggerGlobalSettings(this.swaggerGlobalSettings);
    swaggerApiResourceListing.setSwaggerPathProvider(this.swaggerPathProvider);
    swaggerApiResourceListing.setApiInfo(this.apiInfo);
    swaggerApiResourceListing.setAuthorizationTypes(this.authorizationTypes);
    swaggerApiResourceListing.setAuthorizationContext(this.authorizationContext);
    swaggerApiResourceListing.setModelProvider(this.modelProvider);
    swaggerApiResourceListing.setApiListingReferenceScanner(this.apiListingReferenceScanner);
    swaggerApiResourceListing.setApiVersion(this.apiVersion);
    swaggerApiResourceListing.setApiListingReferenceOrdering(this.apiListingReferenceOrdering);
    swaggerApiResourceListing.setApiDescriptionOrdering(this.apiDescriptionOrdering);
    swaggerApiResourceListing.setRequestMappingEvaluator(requestMappingEvaluator);
    swaggerApiResourceListing.setCustomAnnotationReaders(this.customAnnotationReaders);
  }

  private ApiListingReferenceScanner buildApiListingReferenceScanner() {
    List<Class<? extends Annotation>> mergedExcludedAnnotations = springSwaggerConfig.defaultExcludeAnnotations();
    mergedExcludedAnnotations.addAll(this.excludeAnnotations);

    apiListingReferenceScanner = new ApiListingReferenceScanner();
    apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig
            .swaggerRequestMappingHandlerMappings());
    apiListingReferenceScanner.setResourceGroupingStrategy(this.resourceGroupingStrategy);
    apiListingReferenceScanner.setSwaggerPathProvider(this.swaggerPathProvider);
    apiListingReferenceScanner.setSwaggerGroup(this.swaggerGroup);
    apiListingReferenceScanner.setRequestMappingEvaluator(requestMappingEvaluator);
    apiListingReferenceScanner.setIncludePatterns(this.includePatterns);
    apiListingReferenceScanner.setExcludeAnnotations(mergedExcludedAnnotations);
    return apiListingReferenceScanner;
  }

  public boolean isEnabled() {
    return enabled;
  }
}
