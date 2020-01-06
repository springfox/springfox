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

package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.PathProvider;
import springfox.documentation.annotations.Incubating;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.CodeGenGenericTypeNamingStrategy;
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.contexts.ApiSelector;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spi.service.contexts.SecurityContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;
import static springfox.documentation.schema.AlternateTypeRules.*;

/**
 * A builder which is intended to be the primary interface into the Springfox framework.
 * Provides sensible defaults and convenience methods for configuration.
 */
public class Docket implements DocumentationPlugin {

  public static final String DEFAULT_GROUP_NAME = "default";

  private final DocumentationType documentationType;
  private final List<SecurityContext> securityContexts = new ArrayList<>();
  private final Map<RequestMethod, List<ResponseMessage>> responseMessages = new HashMap<>();
  private final List<Parameter> globalOperationParameters = new ArrayList<>();
  private final List<Function<TypeResolver, AlternateTypeRule>> ruleBuilders = new ArrayList<>();
  private final Set<Class> ignorableParameterTypes = new HashSet<>();
  private final Set<String> protocols = new HashSet<>();
  private final Set<String> produces = new LinkedHashSet<>();
  private final Set<String> consumes = new LinkedHashSet<>();
  private final Set<ResolvedType> additionalModels = new HashSet<>();
  private final Set<Tag> tags = new HashSet<>();

  private PathProvider pathProvider;
  private List<? extends SecurityScheme> securitySchemes;
  private Comparator<ApiListingReference> apiListingReferenceOrdering;
  private Comparator<ApiDescription> apiDescriptionOrdering;
  private Comparator<Operation> operationOrdering;

  private ApiInfo apiInfo = ApiInfo.DEFAULT;
  private String groupName = DEFAULT_GROUP_NAME;
  private boolean enabled = true;
  private GenericTypeNamingStrategy genericsNamingStrategy = new DefaultGenericTypeNamingStrategy();
  private boolean applyDefaultResponseMessages = true;
  private String host = "";
  private Optional<String> pathMapping = empty();
  private ApiSelector apiSelector = ApiSelector.DEFAULT;
  private boolean enableUrlTemplating = false;
  private List<VendorExtension> vendorExtensions = new ArrayList<>();


  public Docket(DocumentationType documentationType) {
    this.documentationType = documentationType;
  }


  /**
   * Add to the api's vendor extensions
   *
   * @param vendorExtensions Indicates the vendor extension information
   * @return this Docket
   */
  public Docket extensions(List<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  /**
   * Sets the api's meta information as included in the json ResourceListing response.
   *
   * @param apiInfo Indicates the api information
   * @return this Docket
   */
  public Docket apiInfo(ApiInfo apiInfo) {
    this.apiInfo = defaultIfAbsent(apiInfo, apiInfo);
    return this;
  }

  /**
   * Configures the global io.swagger.model.SecurityScheme's applicable to all or some of the api
   * operations. The configuration of which operations have associated SecuritySchemes is configured with
   * springfox.swagger.plugins.Docket#securityContexts
   *
   * @param securitySchemes a list of security schemes
   * @return this Docket
   */
  public Docket securitySchemes(List<? extends SecurityScheme> securitySchemes) {
    this.securitySchemes = securitySchemes;
    return this;
  }

  /**
   * Configures which api operations (via regex patterns) and HTTP methods to apply security contexts to apis.
   *
   * @param securityContexts - defines security requirements for the apis
   * @return this Docket
   */
  public Docket securityContexts(List<SecurityContext> securityContexts) {
    this.securityContexts.addAll(securityContexts);
    return this;
  }

  /**
   * If more than one instance of Docket exists, each one must have a unique groupName as
   * supplied by this method. Defaults to "default".
   *
   * @param groupName - the unique identifier of this swagger group/configuration
   * @return this Docket
   */
  public Docket groupName(String groupName) {
    this.groupName = defaultIfAbsent(groupName, this.groupName);
    return this;
  }

  /**
   * Determines the generated, swagger specific, urls.
   * <p>
   * By default, relative urls are generated. If absolute urls are required, supply an implementation of
   * AbsoluteSwaggerPathProvider
   *
   * @param pathProvider - provides an alternate implementation of path provider
   * @return this Docket
   * @see springfox.documentation.spring.web.paths.DefaultPathProvider
   */
  public Docket pathProvider(PathProvider pathProvider) {
    this.pathProvider = pathProvider;
    return this;
  }

  /**
   * Overrides the default http response messages at the http request method level.
   * <p>
   * To set specific response messages for specific api operations use the swagger core annotations on
   * the appropriate controller methods.
   *
   * @param requestMethod    - http request method for which to apply the message
   * @param responseMessages - the message
   * @return this Docket
   * {@code See swagger annotations <code>@ApiResponse</code>, <code>@ApiResponses</code> }.
   * @see springfox.documentation.spi.service.contexts.Defaults#defaultResponseMessages()
   */
  public Docket globalResponseMessage(RequestMethod requestMethod,
                                      List<ResponseMessage> responseMessages) {

    this.responseMessages.put(requestMethod, responseMessages);
    return this;
  }

  /**
   * Adds default parameters which will be applied to all operations.
   *
   * @param operationParameters parameters which will be globally applied to all operations
   * @return this Docket
   */
  public Docket globalOperationParameters(List<Parameter> operationParameters) {
    this.globalOperationParameters.addAll(nullToEmptyList(operationParameters));
    return this;
  }

  /**
   * Adds ignored controller method parameter types so that the framework does not generate swagger model or parameter
   * information for these specific types.
   * e.g. HttpServletRequest/HttpServletResponse which are already included in the pre-configured ignored types.
   *
   * @param classes the classes to ignore
   * @return this Docket
   * @see springfox.documentation.spi.service.contexts.Defaults#defaultIgnorableParameterTypes()
   */
  public Docket ignoredParameterTypes(Class... classes) {
    this.ignorableParameterTypes.addAll(Arrays.asList(classes));
    return this;
  }

  public Docket produces(Set<String> produces) {
    this.produces.addAll(produces);
    return this;
  }

  public Docket consumes(Set<String> consumes) {
    this.consumes.addAll(consumes);
    return this;
  }

  @Incubating("2.3")
  public Docket host(String host) {
    this.host = defaultIfAbsent(host, this.host);
    return this;
  }

  public Docket protocols(Set<String> protocols) {
    this.protocols.addAll(protocols);
    return this;
  }

  /**
   * Adds model substitution rules (alternateTypeRules)
   *
   * @param alternateTypeRules - rules to be applied
   * @return this Docket
   * @see springfox.documentation.schema.AlternateTypeRules#newRule(java.lang.reflect.Type,
   * java.lang.reflect.Type)
   */
  public Docket alternateTypeRules(AlternateTypeRule... alternateTypeRules) {
    this.ruleBuilders.addAll(Stream.of(alternateTypeRules).map(identityRuleBuilder()).collect(toList()));
    return this;
  }

  /**
   * Provide an ordering schema for operations
   * <p>
   * NOTE: @see <a href="https://github.com/springfox/springfox/issues/732">#732</a> in case you're wondering why
   * specifying position might not work.
   *
   * @param operationOrdering - ordering of the operations
   * @return this Docket
   */
  public Docket operationOrdering(Comparator<Operation> operationOrdering) {
    this.operationOrdering = operationOrdering;
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
   * @return this Docket
   */
  public Docket directModelSubstitute(final Class clazz, final Class with) {
    this.ruleBuilders.add(newSubstitutionFunction(clazz, with));
    return this;
  }

  /**
   * Substitutes each generic class with it's direct parameterized type. Use this method to
   * only for types with a single parameterized type. e.g. <code>List&lt;T&gt; or ResponseEntity&lt;T&gt;</code>
   * <code>.genericModelSubstitutes(ResponseEntity.class)</code>
   * would substitute ResponseEntity &lt;MyModel&gt; with MyModel
   *
   * @param genericClasses - generic classes on which to apply generic model substitution.
   * @return this Docket
   */
  public Docket genericModelSubstitutes(Class... genericClasses) {
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
   *              false  - the default response messages are not added to the global response messages
   * @return this Docket
   */
  public Docket useDefaultResponseMessages(boolean apply) {
    this.applyDefaultResponseMessages = apply;
    return this;
  }

  /**
   * Controls how ApiListingReference's are sorted.
   * i.e the ordering of the api's within the swagger Resource Listing.
   * The default sort is Lexicographically by the ApiListingReference's path
   * <p>
   * NOTE: @see <a href="https://github.com/springfox/springfox/issues/732">#732</a> in case you're wondering why
   * specifying position might not work.
   *
   * @param apiListingReferenceOrdering - ordering of the api listing references
   * @return this Docket
   */
  public Docket apiListingReferenceOrdering(Comparator<ApiListingReference> apiListingReferenceOrdering) {
    this.apiListingReferenceOrdering = apiListingReferenceOrdering;
    return this;
  }

  /**
   * Controls how <code>io.swagger.model.ApiDescription</code>'s are ordered.
   * The default sort is Lexicographically by the ApiDescription's path.
   * <p>
   * NOTE: @see <a href="https://github.com/springfox/springfox/issues/732">#732</a> in case you're wondering why
   * specifying position might not work.
   *
   * @param apiDescriptionOrdering - ordering of the api descriptions
   * @return this Docket
   * @see springfox.documentation.spring.web.scanners.ApiListingScanner
   */
  public Docket apiDescriptionOrdering(Comparator<ApiDescription> apiDescriptionOrdering) {
    this.apiDescriptionOrdering = apiDescriptionOrdering;
    return this;
  }

  /**
   * Hook to externally control auto initialization of this swagger plugin instance.
   * Typically used if defer initialization.
   *
   * @param externallyConfiguredFlag - true to turn it on, false to turn it off
   * @return this Docket
   */
  public Docket enable(boolean externallyConfiguredFlag) {
    this.enabled = externallyConfiguredFlag;
    return this;
  }

  /**
   * Set this to true in order to make the documentation code generation friendly
   *
   * @param forCodeGen - true|false determines the naming strategy used
   * @return this Docket
   */
  public Docket forCodeGeneration(boolean forCodeGen) {
    if (forCodeGen) {
      genericsNamingStrategy = new CodeGenGenericTypeNamingStrategy();
    }
    return this;
  }

  /**
   * Extensibility mechanism to add a servlet path mapping, if there is one, to the apis base path.
   *
   * @param path - path that acts as a prefix to the api base path
   * @return this Docket
   */
  public Docket pathMapping(String path) {
    this.pathMapping = ofNullable(path);
    return this;
  }

  /**
   * Decides whether to use url templating for paths. This is especially useful when you have search api's that
   * might have multiple request mappings for each search use case.
   * <p>
   * This is an incubating feature that may not continue to be supported after the swagger specification is modified
   * to accommodate the use case as described in issue #711
   *
   * @param enabled - when true it enables rfc6570 url templates
   * @return this Docket
   */
  @Incubating("2.1.0")
  public Docket enableUrlTemplating(boolean enabled) {
    this.enableUrlTemplating = enabled;
    return this;
  }

  /**
   * Method to add additional models that are not part of any annotation or are perhaps implicit
   *
   * @param first     - at least one is required
   * @param remaining - possible collection of more
   * @return on-going docket
   * @since 2.4.0
   */
  public Docket additionalModels(ResolvedType first, ResolvedType... remaining) {
    additionalModels.add(first);
    additionalModels.addAll(Arrays.stream(remaining).collect(toSet()));
    return this;
  }

  /**
   * Method to add global tags to the docket
   *
   * @param first     - at least one tag is required to use this method
   * @param remaining - remaining tags
   * @return this Docket
   */
  public Docket tags(Tag first, Tag... remaining) {
    tags.add(first);
    tags.addAll(Arrays.stream(remaining).collect(toSet()));
    return this;
  }

  /**
   * Initiates a builder for api selection.
   *
   * @return api selection builder. To complete building the api selector, the build method of the api selector
   * needs to be called, this will automatically fall back to building the docket when the build method is called.
   */
  public ApiSelectorBuilder select() {
    return new ApiSelectorBuilder(this);
  }

  /**
   * Builds the Docket by merging/overlaying user specified values.
   * It is not necessary to call this method when defined as a spring bean.
   * NOTE: Calling this method more than once has no effect.
   *
   * @see DocumentationPluginsBootstrapper
   */
  public DocumentationContext configure(DocumentationContextBuilder builder) {
    return builder
        .apiInfo(apiInfo)
        .selector(apiSelector)
        .applyDefaultResponseMessages(applyDefaultResponseMessages)
        .additionalResponseMessages(responseMessages)
        .additionalOperationParameters(globalOperationParameters)
        .additionalIgnorableTypes(ignorableParameterTypes)
        .ruleBuilders(ruleBuilders)
        .groupName(groupName)
        .pathProvider(pathProvider)
        .securityContexts(securityContexts)
        .securitySchemes(securitySchemes)
        .apiListingReferenceOrdering(apiListingReferenceOrdering)
        .apiDescriptionOrdering(apiDescriptionOrdering)
        .operationOrdering(operationOrdering)
        .produces(produces)
        .consumes(consumes)
        .host(host)
        .protocols(protocols)
        .genericsNaming(genericsNamingStrategy)
        .pathMapping(pathMapping)
        .enableUrlTemplating(enableUrlTemplating)
        .additionalModels(additionalModels)
        .tags(tags)
        .vendorExtentions(vendorExtensions)
        .build();
  }

  @Override
  public String getGroupName() {
    return groupName;
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
    return documentationType.equals(delimiter);
  }

  private Function<AlternateTypeRule, Function<TypeResolver, AlternateTypeRule>> identityRuleBuilder() {
    return this::identityFunction;
  }

  private Function<TypeResolver, AlternateTypeRule> identityFunction(final AlternateTypeRule rule) {
    return typeResolver -> rule;
  }

  Docket selector(ApiSelector apiSelector) {
    this.apiSelector = apiSelector;
    return this;
  }

  private Function<TypeResolver, AlternateTypeRule> newSubstitutionFunction(final Class clazz, final Class with) {
    return typeResolver -> newRule(
        typeResolver.resolve(clazz),
        typeResolver.resolve(with),
        DIRECT_SUBSTITUTION_RULE_ORDER);
  }

  private Function<TypeResolver, AlternateTypeRule> newGenericSubstitutionFunction(final Class clz) {
    return typeResolver -> newRule(
        typeResolver.resolve(clz, WildcardType.class),
        typeResolver.resolve(WildcardType.class),
        GENERIC_SUBSTITUTION_RULE_ORDER);
  }
}
