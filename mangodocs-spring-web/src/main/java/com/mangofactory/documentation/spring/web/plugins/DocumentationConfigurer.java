package com.mangofactory.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Function;
import com.google.common.collect.Ordering;
import com.mangofactory.documentation.schema.AlternateTypeRule;
import com.mangofactory.documentation.schema.WildcardType;
import com.mangofactory.documentation.service.PathProvider;
import com.mangofactory.documentation.service.RequestMappingPatternMatcher;
import com.mangofactory.documentation.service.model.ApiDescription;
import com.mangofactory.documentation.service.model.ApiInfo;
import com.mangofactory.documentation.service.model.ApiListingReference;
import com.mangofactory.documentation.service.model.AuthorizationType;
import com.mangofactory.documentation.service.model.ResponseMessage;
import com.mangofactory.documentation.service.model.builder.ApiInfoBuilder;
import com.mangofactory.documentation.spi.DocumentationType;
import com.mangofactory.documentation.spi.service.DocumentationPlugin;
import com.mangofactory.documentation.spi.service.contexts.AuthorizationContext;
import com.mangofactory.documentation.spi.service.contexts.DocumentationContextBuilder;
import com.mangofactory.documentation.spring.web.ordering.ApiDescriptionLexicographicalOrdering;
import com.mangofactory.documentation.spring.web.ordering.ResourceListingLexicographicalOrdering;
import com.mangofactory.documentation.spring.web.scanners.RegexRequestMappingPatternMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;
import static com.mangofactory.documentation.schema.AlternateTypeRules.*;
import static java.util.Arrays.asList;
import static org.springframework.util.StringUtils.*;

/**
 * A builder which is intended to be the primary interface into the swagger-springmvc framework.
 * Provides sensible defaults and convenience methods for configuration.
 */
public class DocumentationConfigurer implements DocumentationPlugin {

  private final DocumentationType documentationType;
  private String groupName;
  private ApiInfo apiInfo;
  private PathProvider pathProvider;
  private AuthorizationContext authorizationContext;
  private List<AuthorizationType> authorizationTypes;

  private AtomicBoolean initialized = new AtomicBoolean(false);
  private boolean enabled = true;
  private List<String> includePatterns = newArrayList(".*?");
  private List<Class<? extends Annotation>> excludeAnnotations = newArrayList();
  private boolean applyDefaultResponseMessages = true;
  private Map<RequestMethod, List<ResponseMessage>> responseMessages = newHashMap();
  private Set<Class> ignorableParameterTypes = newHashSet();
  private RequestMappingPatternMatcher requestMappingPatternMatcher = new RegexRequestMappingPatternMatcher();
  private List<Function<TypeResolver, AlternateTypeRule>> ruleBuilders = newArrayList();
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
   * @see PathProvider
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
   * Under the hood, <code>com.mangofactory.documentation.service.RequestMappingPatternMatcher</code>is used to match a
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
   * @see com.mangofactory.documentation.spi.service.contexts.Defaults#defaultResponseMessages()
   */
  public DocumentationConfigurer globalResponseMessage(RequestMethod requestMethod,
                                                       List<ResponseMessage> responseMessages) {

    this.responseMessages.put(requestMethod, responseMessages);
    return this;
  }

  /**
   * Adds ignored controller method parameter types so that the framework does not generate swagger model or parameter
   * information for these specific types.
   * e.g. HttpServletRequest/HttpServletResponse which are already included in the pre-configured ignored types.
   *
   * @param classes the classes to ignore
   * @return this DocumentationConfigurer
   * @see com.mangofactory.documentation.spi.service.contexts.Defaults#defaultIgnorableParameterTypes()
   */
  public DocumentationConfigurer ignoredParameterTypes(Class... classes) {
    this.ignorableParameterTypes.addAll(Arrays.asList(classes));
    return this;
  }

  /**
   * Adds model substitution rules (alternateTypeRules)
   *
   * @param alternateTypeRules
   * @return this DocumentationConfigurer
   * @see com.mangofactory.documentation.schema.AlternateTypeRules#newRule(java.lang.reflect.Type,
   * java.lang.reflect.Type)
   */
  public DocumentationConfigurer alternateTypeRules(AlternateTypeRule... alternateTypeRules) {
    this.ruleBuilders.addAll(from(newArrayList(alternateTypeRules)).transform(identityRuleBuilder()).toList());
    return this;
  }

  private Function<AlternateTypeRule, Function<TypeResolver, AlternateTypeRule>> identityRuleBuilder() {
    return new Function<AlternateTypeRule, Function<TypeResolver, AlternateTypeRule>>() {
      @Override
      public Function<TypeResolver, AlternateTypeRule> apply(AlternateTypeRule rule) {
        return identityFunction(rule);
      }
    };
  }

  private Function<TypeResolver, AlternateTypeRule> identityFunction(final AlternateTypeRule rule) {
    return new Function<TypeResolver, AlternateTypeRule>() {
      @Override
      public AlternateTypeRule apply(TypeResolver typeResolver) {
        return rule;
      }
    };
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
    this.ruleBuilders.add(newSubstitutionFunction(clazz, with));
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
      this.ruleBuilders.add(newGenericSubstitutionFunction(clz));
    }
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
   * @see com.mangofactory.documentation.spring.web.scanners.ApiListingScanner
   */
  public DocumentationConfigurer apiDescriptionOrdering(Ordering<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    return this;
  }

  /**
   * Hook for adding custom annotations readers. Useful when you want to add your own annotation to be mapped to swagger
   * model.
   *
   * @param requestMappingPatternMatcher an implementation of {@link com.mangofactory.documentation.spring.web.scanners
   *                                     .RequestMappingPatternMatcher}. Out of the box the library comes with
   *                                     {@link com.mangofactory.documentation.spring.web.scanners
   *                                     .RegexRequestMappingPatternMatcher} and
   *                                     {@link com.mangofactory.documentation.spring.web.scanners
   *                                     .AntRequestMappingPatternMatcher}
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
   * @see com.mangofactory.documentation.spring.web.plugins.DocumentationPluginsBootstrapper
   */
  public void configure(DocumentationContextBuilder builder) {
    if (initialized.compareAndSet(false, true)) {
      configureDefaults(builder);
    }
    builder
            .apiInfo(apiInfo)
            .applyDefaultResponseMessages(applyDefaultResponseMessages)
            .additionalResponseMessages(responseMessages)
            .additionalIgnorableTypes(ignorableParameterTypes)
            .additionalExcludedAnnotations(excludeAnnotations)
            .includePatterns(includePatterns)
            .ruleBuilders(ruleBuilders)
            .requestMappingPatternMatcher(requestMappingPatternMatcher)
            .groupName(groupName)
            .pathProvider(pathProvider)
            .authorizationContext(authorizationContext)
            .authorizationTypes(authorizationTypes)
            .apiListingReferenceOrdering(apiListingReferenceOrdering)
            .apiDescriptionOrdering(apiDescriptionOrdering);
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

  private void configureDefaults(DocumentationContextBuilder defaults) {
    if (!hasText(this.groupName)) {
      this.groupName = "default";
    }

    if (null == this.apiInfo) {
      this.apiInfo = defaultApiInfo();
    }
  }


}
