# Swagger / Spring MVC Integration
- Coverage: [![Coverage Status](https://coveralls.io/repos/martypitt/swagger-springmvc/badge.png?branch=master)]
(https://coveralls.io/r/martypitt/swagger-springmvc?branch=master)

- CI: [![Build Status](https://travis-ci.org/martypitt/swagger-springmvc.png?branch=master)](https://travis-ci
.org/martypitt/swagger-springmvc)

### About
An upgrade to the swagger-springmvc project to the latest Swagger specification (1.2).
The swagger specification upgrade to 1.2 has several feature additions and has also refactored it's core model structure.
See [swagger-core](https://github.com/wordnik/swagger-core)
The [demo/sample](https://github.com/adrianbk/swagger-springmvc-demo) application is built off of swagger-ui tag `v2.0.4`

There are currently some features not fully supported:
- Model generation - work in progress to modularize the model generation from prior versions of swagger-springmvc. This version uses the model
generation that ships with swagger-core which is not as powerful when it comes to polymorphic models.

### Sample Application

[https://github.com/adrianbk/swagger-springmvc-demo](https://github.com/adrianbk/swagger-springmvc-demo)

###### Snapshot version
```xml
  <repositories>
    <repository>
      <id>sonatype-snapshots</id>
      <name>Sonatype</name>
      <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </repository>
  </repositories>

  <dependency>
    <groupId>com.mangofactory</groupId>
    <artifactId>swagger-springmvc</artifactId>
    <version>0.8.4-SNAPSHOT</version>
  </dependency>
```

###### Release version
or maven central: http://repo1.maven.org/maven2/

```xml

  <repositories>
    <repository>
      <id>sonatype-snapshots</id>
      <name>Sonatype</name>
      <url>https://oss.sonatype.org/content/repositories/releases/</url>
    </repository>
  </repositories>

  <dependency>
    <groupId>com.mangofactory</groupId>
    <artifactId>swagger-springmvc</artifactId>
    <version>0.8.3</version>
  </dependency>
```


### Swagger Spec 1.2 changes:
- Authorization types: (OAuth, ApiKey, BasicAuth).
- ApiInfo: info, title, licencing, etc.
- Http media types (produces/consumes).
- Model annotation changes.
- resource & base path changes.
- Swagger core library has upgraded to scala 2.10.0.
For more detail see: https://github.com/wordnik/swagger-core/wiki/1.2-transition.




##### Notable Dependencies
- Spring 3.2.x or above 
- scala lib 2.10.0
- jackson 2.1.5 (older/newer versions may work)

##### Documentation and Javadocs
By no means is the documentation great but the project has plenty of tests and we're slowly chipping away at the
documentation. The latest javadocs are available [here](http://martypitt.github.io/swagger-springmvc/). Contributions
that add to test coverage and documentation is always welcome! :)

#### Changes

[Change Log](History.md) is available for the changes since 0.8.3

##### Breaking changes in 0.8.4 since 0.8.2 that would need the configurations to be altered are:
1. Remove the following autowired fields in your spring configuration
```java
   @Autowired
   private SpringSwaggerModelConfig springSwaggerModelConfig;
```
2. Add the following autowired fields in your spring configuration
```java
  @Autowired
  private ModelProvider modelProvider;
```
3. Make sure any customizations to the object mapper are appropriately added in an ObjectMapper bean definition
```java
    /**
        * Object mapper.
        *
        * @return the configured object mapper
        */
        @Bean
        public ObjectMapper objectMapper() {
            //This is the opportunity to override object mapper behavior
            return new ObjectMapper();
        }
```
4. Configure the swaggerApiResourceListing bean with the model provider that is autowired or provide your own
implementation
```java
  // Set the model provider, uses the default autowired model provider.
      swaggerApiResourceListing.setModelProvider(modelProvider);
```

5. Configure the blah bean with an implementation of the resource grouping strategy
```java
    //How to group request mappings to ApiResource's typically by spring controller classes. This is a hook to provide
     // a custom implementation of the grouping strategy. By default we use SpringGroupingStrategy. An alternative is
     // to use ClassOrApiAnnotationResourceGrouping to group using Api annotation.
        apiListingReferenceScanner.setResourceGroupingStrategy(springSwaggerConfig.defaultResourceGroupingStrategy());
```

##### Features
- Allows configuration of default response messages based on HTTP methods which are displayed on all api operations on swagger-ui

E.g. Default response messages for HTTP GET methods
```java
 responses.put(GET, asList(
            new ResponseMessage(OK.value(), OK.getReasonPhrase(), toOption(null)),
            new ResponseMessage(NOT_FOUND.value(), NOT_FOUND.getReasonPhrase(), toOption(null)),
            new ResponseMessage(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase(), toOption(null)),
            new ResponseMessage(UNAUTHORIZED.value(), UNAUTHORIZED.getReasonPhrase(), toOption(null))
  ));
```


Configurable global ignored spring mvc controller method parameters/`HandlerMethodArgumentResolver`'s
Annotation classes can also be added here to ignore method parameters with a specific annotation.

E.g.
```java
     HashSet<Class> ignored = newHashSet();
      ignored.add(ServletRequest.class);
      ignored.add(ServletResponse.class);
      ignored.add(HttpServletRequest.class);
      ignored.add(HttpServletResponse.class);
      ignored.add(BindingResult.class);
      ignored.add(ServletContext.class);
```

- Configurable swagger type substitutions

E.g.
```Java

    AlternateTypeProvider alternateTypeProvider = new AlternateTypeProvider();
    TypeResolver typeResolver = new TypeResolver(); //dependency on com.fasterxml.classmate
    // Add a rule that substitutes ResponseEntity<AnyClass> to AnyClass
    // NOTE: WildcardType is an in-built type used for type substitutions of generic
    //      types
    alternateTypeProvider.addRule(
      newRule(typeResolver.resolve(ResponseEntity.class, WildcardType.class),
              typeResolver.resolve(WildcardType.class)));
              
    // Add a rule that substitutes LocalDate to Date
    alternateTypeProvider.addRule(
      newRule(typeResolver.resolve(LocalDate.class),
              typeResolver.resolve(Date.class)));
              
    //After setting up the custom provider wire it up by calling swaggerGlobalSettings.setAlternateTypeProvider

```

- Configurable uri path providers by implementing `SwaggerPathProvider`

- Exclude controller methods based on annotations

E.g.
```java
  List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
  annotations.add(ApiIgnore.class);
```

- Flexible Inclusion or exclusion of paths using regex expressions. **Make sure the include patterns apply**
```java
List<String> DEFAULT_INCLUDE_PATTERNS = Arrays.asList(new String[]{
      "/business.*",
      "/some.*",
      "/contacts.*"
  });
```

### Adding to to a spring MVC application

web application context xml config

```xml
  <!-- Enable scanning of spring @Configuration classes -->
  <context:annotation-config/>

  <!-- Enable the default documentation controller-->
  <context:component-scan base-package="com.mangofactory.swagger.controllers"/>

  <!-- Pick up the bundled spring config. Not really required if you're already importing the configuration bean
  as part of an application specific configuration bean via the previous component scan-->
  <context:component-scan base-package="com.mangofactory.swagger.configuration"/>

```

##### Configuration
Configuration is slightly verbose but on the upside it provides several hooks into the library.
- Place the following into a spring @Configuration java class or see: [The Sample App Configuration](https://github.com/adrianbk/swagger-springmvc-demo/blob/master/src/main/java/com/ak/swaggermvc/demo/config/SwaggerConfig.java)

```java

  /**
   * 
   * Autowire the bundled swagger config
   */
  @Autowired
  private SpringSwaggerConfig springSwaggerConfig;
  @Autowired
  private ModelProvider modelProvider;



  /**
   * Adds the jackson scala module to the MappingJackson2HttpMessageConverter registered with spring
   * Swagger core models are scala so we need to be able to convert to JSON
   * Also registers some custom serializers needed to transform swagger models to swagger-ui required json format
   */
  @Bean
  public JacksonScalaSupport jacksonScalaSupport() {
    JacksonScalaSupport jacksonScalaSupport = new JacksonScalaSupport();
    //Set to false to disable
    jacksonScalaSupport.setRegisterScalaModule(true);
    return jacksonScalaSupport;
  }

  /**
    * Object mapper.
    *
    * @return the configured object mapper
    */
    @Bean
    public ObjectMapper objectMapper() {
        //This is the opportunity to override object mapper behavior
        return new ObjectMapper();
    }

  /**
   * Global swagger settings
   */
  @Bean
  public SwaggerGlobalSettings swaggerGlobalSettings() {
    SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
    swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages());

    // This is where we add types to ignore (or use the default provided types)
    swaggerGlobalSettings.setIgnorableParameterTypes(springSwaggerConfig.defaultIgnorableParameterTypes());
    // This is where we add type substitutions (or use the default provided alternates)
    swaggerGlobalSettings.setAlternateTypeProvider(springSwaggerConfig.defaultAlternateTypeProvider());
    return swaggerGlobalSettings;
  }

  /**
   * API Info as it appears on the swagger-ui page
   */
  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfo(
        "Demo Spring MVC swagger 1.2 api",
        "Sample spring mvc api based on the swagger 1.2 spec",
        "http://en.wikipedia.org/wiki/Terms_of_service",
        "somecontact@somewhere.com",
        "Apache 2.0",
        "http://www.apache.org/licenses/LICENSE-2.0.html"
    );
    return apiInfo;
  }

  /**
   * Configure a SwaggerApiResourceListing for each swagger instance within your app. e.g. 1. private  2. external apis
   * Required to be a spring bean as spring will call the postConstruct method to bootstrap swagger scanning.
   *
   * @return
   */
  @Bean
  public SwaggerApiResourceListing swaggerApiResourceListing() {
    //The group name is important and should match the group set on ApiListingReferenceScanner
    //Note that swaggerCache() is by DefaultSwaggerController to serve the swagger json
    SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(springSwaggerConfig.swaggerCache(), SWAGGER_GROUP);

    //Set the required swagger settings
    swaggerApiResourceListing.setSwaggerGlobalSettings(swaggerGlobalSettings());

    //Use a custom path provider or springSwaggerConfig.defaultSwaggerPathProvider()
    swaggerApiResourceListing.setSwaggerPathProvider(demoPathProvider());

    //Supply the API Info as it should appear on swagger-ui web page
    swaggerApiResourceListing.setApiInfo(apiInfo());

    // Set the model provider, uses the default autowired model provider.
    swaggerApiResourceListing.setModelProvider(modelProvider);

    //Global authorization - see the swagger documentation
    swaggerApiResourceListing.setAuthorizationTypes(authorizationTypes());

    //Sets up an auth context - i.e. which controller request paths to apply global auth to
    swaggerApiResourceListing.setAuthorizationContext(authorizationContext());

    //Every SwaggerApiResourceListing needs an ApiListingReferenceScanner to scan the spring request mappings
    swaggerApiResourceListing.setApiListingReferenceScanner(apiListingReferenceScanner());
    return swaggerApiResourceListing;
  }

  @Bean
  /**
   * The ApiListingReferenceScanner does most of the work.
   * Scans the appropriate spring RequestMappingHandlerMappings
   * Applies the correct absolute paths to the generated swagger resources
   */
  public ApiListingReferenceScanner apiListingReferenceScanner() {
    ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();

    //Picks up all of the registered spring RequestMappingHandlerMappings for scanning
    apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig.swaggerRequestMappingHandlerMappings());

    //Excludes any controllers with the supplied annotations
    apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig.defaultExcludeAnnotations());

    //How to group request mappings to ApiResource's typically by spring controller clesses or @Api.value() 
    apiListingReferenceScanner.setResourceGroupingStrategy(springSwaggerConfig.defaultResourceGroupingStrategy());

    //Path provider used to generate the appropriate uri's
    apiListingReferenceScanner.setSwaggerPathProvider(demoPathProvider());

    //Must match the swagger group set on the SwaggerApiResourceListing
    apiListingReferenceScanner.setSwaggerGroup(SWAGGER_GROUP);

    //Only include paths that match the supplied regular expressions
    apiListingReferenceScanner.setIncludePatterns(DEFAULT_INCLUDE_PATTERNS);

    return apiListingReferenceScanner;
  }

  /**
   * Example of a custom path provider
   */
  @Bean
  public DemoPathProvider demoPathProvider() {
    DemoPathProvider demoPathProvider = new DemoPathProvider();
    demoPathProvider.setDefaultSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
    return demoPathProvider;
  }
```


##Development

- Running tests with coverage:
```
mvn test jacoco:check
```
Report directory: \target\site\jacoco-ut

Coverage only
```
mvn jacoco:check
```

Deploy to local nexus
```
mvn deploy
```

-Coverage Help
```
mvn org.jacoco:jacoco-maven-plugin:0.6.3.201306030806:check
```

Skipping coverage
```
mvn deploy -Djacoco.skip=true
```

Pre Commit or before submitting a pull request
```
mvn verify
```
License
-------

Copyright 2012 Marty Pitt - [@martypitt](https://github.com/martypitt), Dilip Krishnan - [@dilipkrish](https://github.com/dilipkrish),
Adrian Kelly -  [@adrianbk](https://github.com/adrianbk),

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


