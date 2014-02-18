# Swagger / Spring MVC Integration

[![Build Status](https://travis-ci.org/adrianbk/swagger-springmvc.png?branch=swagger-spec-1.2.0-upgrade)](https://travis-ci.org/adrianbk/swagger-springmvc)


### About
An upgrade to the swagger-springmvc project to the latest Swagger specification (1.2)
The swagger specification upgrade to 1.2 has several feature additions and has also refactored it's core model structure.
See [swagger-core](https://github.com/wordnik/swagger-core)
The [demo/sample](https://github.com/adrianbk/swagger-springmvc-demo) application is built off of swagger-ui tag `v2.0.4`

There are currently some features not fully supported:
- Model generation - work in progress to modularize the model generation from prior versions of swagger-springmvc. This version uses the model
generation that ships with swagger-core which is not as powerful when it comes to polymorphic models

- Oauth/authorization - not yet appearing on swagger-ui page as there is some further work required to transform the swagger-core models to
the JSON structure required by swagger-ui.

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
    <version>0.8.1-SNAPSHOT</version>
  </dependency>
```

###### Release version
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
    <version>0.8.1</version>
  </dependency>
```


###### Swagger Spec 1.2 changes:
- Authorization types: (OAuth, ApiKey, BasicAuth) (swagger-ui has not yet upgraded to support these)
- ApiInfo: info, title, licencing, etc.
- Http media types (produces/consumes)
- Model annotation changes
- resource & base path changes
- Swagger core library has upgraded to scala 2.10.0
For more detail see: https://github.com/wordnik/swagger-core/wiki/1.2-transition


##### Summary of features/changes to prior swagger-springmvc library
- Supports multiple instances of swagger api resource listings from the same spring mvc application
- Authorization types.
- HTTP media types
- Request mappings with regex expressions do not error out
- All http methods supported by org.springframework.web.bind.annotation.RequestMethod (GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)
- Filtering/Inclusion of api endpoints with regular expression or ant path matching.
- Operation parameter data types supported as per spec: https://github.com/wordnik/swagger-core/wiki/Datatypes
- Http response codes and messages with com.wordnik.swagger.annotations.ApiResponses
- All uris are, by default, absolute after much deliberation. Relative uri's do not work well with swagger UI and other swagger tools like
  swagger-codegen work much better with absolute uri's. This strategy can be changed by
  implementing [SwaggerPathProvider](https://github.com/adrianbk/swagger-springmvc/blob/swagger-spec-1.2.0-upgrade/src/main/java/com/mangofactory/swagger/core/SwaggerPathProvider.java)
  This strategy is also useful if your api sits behind a proxy like mashery

##### Notable Dependencies
- Spring 3.1.1 or above (due to a bug in UriComponentsBuilder)
- scala lib 2.10.0
- jackson 2.1.5 (older/newer versions may work)

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

- Configurable global ignored spring mvc controller method parameters/`HandlerMethodArgumentResolver`'s

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

- Configurable swagger parameter data type mappings

E.g.
```Java
      Map<Class, String> dataTypeMappings = newHashMap();
      dataTypeMappings.put(char.class, "string");
      dataTypeMappings.put(String.class, "string");
      dataTypeMappings.put(Integer.class, "int32");
      dataTypeMappings.put(int.class, "int32");
```

- Configurable uri path providers by implementing `SwaggerPathProvider`

- Exclude controller methods based on annotations

E.g.
```java
  List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>();
  annotations.add(ApiIgnore.class);
```

- Flexible Inclusion or exclusion of paths using regex expressions
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

  <!-- Pick up the bundled spring config-->
  <context:component-scan base-package="com.mangofactory.swagger.configuration"/>

```

##### Configuration
Configuration is slightly verbose but on the upside it provides several hooks into the library.
- Place the following into a spring @Configuration java class or see: [The Sample App Configuration](https://github.com/adrianbk/swagger-springmvc-demo/blob/master/src/main/java/com/ak/swaggermvc/demo/config/SwaggerConfig.java)

```java
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
   * Global swagger settings
   */
  @Bean
  public SwaggerGlobalSettings swaggerGlobalSettings() {
    SwaggerGlobalSettings swaggerGlobalSettings = new SwaggerGlobalSettings();
    swaggerGlobalSettings.setGlobalResponseMessages(springSwaggerConfig.defaultResponseMessages());
    swaggerGlobalSettings.setIgnorableParameterTypes(springSwaggerConfig.defaultIgnorableParameterTypes());
    swaggerGlobalSettings.setParameterDataTypes(springSwaggerModelConfig.defaultParameterDataTypes());
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

    //
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

