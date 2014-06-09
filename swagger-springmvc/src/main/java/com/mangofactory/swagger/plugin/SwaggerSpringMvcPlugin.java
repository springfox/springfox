package com.mangofactory.swagger.plugin;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Ordering;
import com.mangofactory.swagger.authorization.AuthorizationContext;
import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import com.mangofactory.swagger.core.ResourceGroupingStrategy;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.mangofactory.swagger.models.ModelProvider;
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider;
import com.mangofactory.swagger.models.alternates.AlternateTypeRule;
import com.mangofactory.swagger.models.alternates.WildcardType;
import com.mangofactory.swagger.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.swagger.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.swagger.paths.SwaggerPathProvider;
import com.mangofactory.swagger.scanners.ApiListingReferenceScanner;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.ApiInfo;
import com.wordnik.swagger.model.ApiListingReference;
import com.wordnik.swagger.model.AuthorizationType;
import com.wordnik.swagger.model.ResponseMessage;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mangofactory.swagger.models.alternates.Alternates.newRule;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * A builder which is intended to be the primary interface into the framework.
 * Provides sensible defaults and convenience methods to configure swagger-springmvc.
 */
public class SwaggerSpringMvcPlugin {

  private ModelProvider modelProvider;
  private String swaggerGroup;
  private List<String> includePatterns;
  private SwaggerPathProvider swaggerPathProvider;
  private List<AuthorizationType> authorizationTypes;
  private ApiInfo apiInfo;
  private AuthorizationContext authorizationContext;
  private List<Class<? extends Annotation>> excludeAnnotations = new ArrayList<Class<? extends Annotation>>();
  private ResourceGroupingStrategy resourceGroupingStrategy;
  private String apiVersion = "1.0";

  private SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
  private Map<RequestMethod, List<ResponseMessage>> globalResponseMessages = new HashMap<RequestMethod,
          List<ResponseMessage>>();
  private Set<Class> ignorableParameterTypes = new HashSet<Class>();
  private AlternateTypeProvider alternateTypeProvider;
  private List<AlternateTypeRule> alternateTypeRules = new ArrayList<AlternateTypeRule>();
  private SpringSwaggerConfig springSwaggerConfig;
  private SwaggerApiResourceListing swaggerApiResourceListing;
  private Ordering<ApiListingReference> apiListingReferenceOrdering = new ResourceListingLexicographicalOrdering();
  private Ordering<ApiDescription> apiDescriptionOrdering = new ApiDescriptionLexicographicalOrdering();
  private ApiListingReferenceScanner apiListingReferenceScanner;

  public SwaggerSpringMvcPlugin(SpringSwaggerConfig springSwaggerConfig) {
    Assert.notNull(springSwaggerConfig);
    this.springSwaggerConfig = springSwaggerConfig;
  }

  public SwaggerSpringMvcPlugin build() {
    configure();
    buildSwaggerGlobalSettings();
    buildApiListingReferenceScanner();
    buildSwaggerApiResourceListing();
    return this;
  }

  private void configure() {
    if (isBlank(this.swaggerGroup)) {
      this.swaggerGroup = "default";
    }

    if (null == this.apiInfo) {
      this.apiInfo = defaultApiInfo();
    }

    if (null == this.resourceGroupingStrategy) {
      this.resourceGroupingStrategy = springSwaggerConfig.defaultResourceGroupingStrategy();
    }

    if (null == this.includePatterns || this.includePatterns.size() == 0) {
      this.includePatterns = asList(".*?");
    }

    if (null == swaggerPathProvider) {
      this.swaggerPathProvider = springSwaggerConfig.defaultSwaggerPathProvider();
    }

    if (null == this.alternateTypeProvider) {
      this.alternateTypeProvider = springSwaggerConfig.defaultAlternateTypeProvider();
    }

    if (null == this.modelProvider) {
      this.modelProvider = springSwaggerConfig.defaultModelProvider();
    }
  }

  private void buildSwaggerGlobalSettings() {
    Map<RequestMethod, List<ResponseMessage>> mergedResponseMessages = new HashMap<RequestMethod,
            List<ResponseMessage>>();
    mergedResponseMessages.putAll(springSwaggerConfig.defaultResponseMessages());
    mergedResponseMessages.putAll(this.globalResponseMessages);
    swaggerGlobalSettings.setGlobalResponseMessages(mergedResponseMessages);

    Set<Class> mergedIgnorableParameterTypes = new HashSet<Class>();
    mergedIgnorableParameterTypes.addAll(springSwaggerConfig.defaultIgnorableParameterTypes());
    mergedIgnorableParameterTypes.addAll(this.ignorableParameterTypes);
    swaggerGlobalSettings.setIgnorableParameterTypes(mergedIgnorableParameterTypes);

    for (AlternateTypeRule rule : this.alternateTypeRules) {
      this.alternateTypeProvider.addRule(rule);
    }
    swaggerGlobalSettings.setAlternateTypeProvider(this.alternateTypeProvider);
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

  }

  private ApiListingReferenceScanner buildApiListingReferenceScanner() {
    List<Class<? extends Annotation>> mergedExcludedAnnotations = springSwaggerConfig.defaultExcludeAnnotations();
    mergedExcludedAnnotations.addAll(this.excludeAnnotations);

    apiListingReferenceScanner = new ApiListingReferenceScanner();
    apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig
            .swaggerRequestMappingHandlerMappings());
    apiListingReferenceScanner.setExcludeAnnotations(mergedExcludedAnnotations);
    apiListingReferenceScanner.setResourceGroupingStrategy(this.resourceGroupingStrategy);
    apiListingReferenceScanner.setSwaggerPathProvider(this.swaggerPathProvider);
    apiListingReferenceScanner.setSwaggerGroup(this.swaggerGroup);
    apiListingReferenceScanner.setIncludePatterns(this.includePatterns);
    return apiListingReferenceScanner;
  }

  public SwaggerSpringMvcPlugin apiInfo(ApiInfo apiInfo) {
    this.apiInfo = apiInfo;
    return this;
  }

  public SwaggerSpringMvcPlugin swaggerGroup(String swaggerGroup) {
    this.swaggerGroup = swaggerGroup;
    return this;
  }

  public SwaggerSpringMvcPlugin pathProvider(SwaggerPathProvider swaggerPathProvider) {
    this.swaggerPathProvider = swaggerPathProvider;
    return this;
  }

  public SwaggerSpringMvcPlugin authorizationTypes(List<AuthorizationType> authorizationTypes) {
    this.authorizationTypes = authorizationTypes;
    return this;
  }

  public SwaggerSpringMvcPlugin authorizationContext(AuthorizationContext authorizationContext) {
    this.authorizationContext = authorizationContext;
    return this;
  }

  public SwaggerSpringMvcPlugin excludeAnnotations(Class<? extends Annotation>... excludeAnnotations) {
    this.excludeAnnotations.addAll(asList(excludeAnnotations));
    return this;
  }

  /**
   * Controls which controllers, more specifically, which Spring RequestMappings to include in the swagger Resource
   * Listing.
   * <p/>
   * Under the hood, <code>com.mangofactory.swagger.scanners.RequestMappingPatternMatcher</code>is used to match a
   * given <code>org.springframework.web.servlet.mvc.condition.PatternsRequestCondition</code> against the
   * includePatterns supplied here.
   * <p/>
   * <code>RegexRequestMappingPatternMatcher</code> is the default implementation and requires these includePatterns to
   * be valid regular expressions.
   * <p/>
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
   * <p/>
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
   * information for specific types.
   * e.g. HttpServletRequest/HttpServletResponse which are included in pre-configured ignored types.
   * This method adds to the pre-configured ignored types:
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
   * @return
   */
  public SwaggerSpringMvcPlugin apiVersion(String apiVersion) {
    Assert.hasText(apiVersion, "apiVersion must contain text");
    this.apiVersion = apiVersion;
    return this;
  }

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
   * Substitutes each generic class with it's direct parameterized type.
   * e.g.
   * <code>.genericModelSubstitutes(ResponseEntity.class)</code>
   * would substitute ResponseEntity<MyModel> with MyModel
   *
   * @param genericClasses - generic classes on which to apply generic model substitution.
   * @return this SwaggerSpringMvcPlugin
   */
  public SwaggerSpringMvcPlugin genericModelSubstitutes(Class... genericClasses) {
    TypeResolver typeResolver = swaggerGlobalSettings.getTypeResolver();
    for (Class clz : genericClasses) {
      this.alternateTypeRules.add(
              newRule(typeResolver.resolve(clz, WildcardType.class), typeResolver.resolve(WildcardType.class))
                                 );
    }
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
   * @see com.mangofactory.swagger.scanners.ApiListingScanner
   * @param apiDescriptionOrdering
   * @return
   */
  public SwaggerSpringMvcPlugin apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
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
    this.build().swaggerApiResourceListing.initialize();
  }
}
