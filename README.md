# Swagger-springmvc
[![Download](https://api.bintray.com/packages/swaggerspringmvc/swaggerspringmvc/swagger-springmvc/images/download.svg) ](https://bintray.com/swaggerspringmvc/swaggerspringmvc/swagger-springmvc/_latestVersion)

| Unit  | Functional   | Coverage   |
|---|---|---|
|[![Circle CI](https://circleci.com/gh/springfox/springfox/tree/master.svg?style=svg)](https://circleci.com/gh/springfox/springfox/tree/master)|[![Build Status](https://travis-ci.org/adrianbk/swagger-springmvc-demo.svg?branch=master)](https://travis-ci.org/adrianbk/swagger-springmvc-demo) |[![Coverage Status](https://coveralls.io/repos/springfox/springfox/badge.svg)](https://coveralls.io/r/springfox/springfox) |

### About

This project integrates swagger with the Spring Web MVC framework. The complete swagger specification is available
at https://github.com/swagger-api/swagger-spec and it's worth being familiar with the main concepts of the specification and the documentation on the [Swagger Annotations] (https://github.com/swagger-api/swagger-core/wiki/Annotations)
Typically a Spring Web MVC project will use this project in combination with the swagger-ui project (https://github.com/swagger-api/swagger-ui)
to provide the user interface which visualises an applications JSON api's. The most common know use of this project has been
Spring Web MVC applications using springs `MappingJackson2HttpMessageConverter` to produce JSON API endpoints.

The demo project (https://github.com/adrianbk/swagger-springmvc-demo) contains a number of examples using both spring
web mvc and spring-boot.

### Core Contributors
More details on the core contributors is available [here](http://springfox.github.io/springfox/contributors.html)

### Development and contribution guidelines are available [here](https://github.com/martypitt/swagger-springmvc/wiki/Development)

### Repositories

#### Release version
**NOTE:** Version 2.x has not yet been released. 
To use 1.0.2 released version please browse to the pre-2.0 [tag](https://github.com/springfox/springfox/tree/pre-2.0)

__Maven__
```xml
<repositories>
    <repository>
      <id>jcenter-snapshots</id>
      <name>jcenter</name>
      <url>http://oss.jfrog.org/artifactory/oss-snapshot-local/</url>
    </repository>
</repositories>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-core</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-spi</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-spring-web</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.0.0</version>
</dependency>
<!-- for swagger spec 1.2 optionally -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger</artifactId>
    <version>2.0.0</version>
</dependency>
```

__Gradle__

```groovy

repositories {
   maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local/' }
}

compile "io.springfox:springfox-core:2.0.0"
compile "io.springfox:springfox-spi:2.0.0"
compile "io.springfox:springfox-schema:2.0.0"
compile "io.springfox:springfox-spring-web:2.0.0"
compile "io.springfox:springfox-swagger2:2.0.0"
//Optional for swagger 1.2
compile "io.springfox:springfox-swagger:2.0.0"
```

#### Snapshot version

The build file/pom will require the following changes
__Maven__
```xml
<repositories>
    <repository>
      <id>jcenter-snapshots</id>
      <name>jcenter</name>
      <url>http://oss.jfrog.org/artifactory/oss-snapshot-local/</url>
    </repository>
</repositories>

<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-core</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-spi</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-spring-web</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
<!-- for swagger 1.2 optionally -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

__Gradle__

```groovy

repositories {
   maven { url 'http://oss.jfrog.org/artifactory/oss-snapshot-local/' }
}

compile "io.springfox:springfox-core:2.0.0-SNAPSHOT"
compile "io.springfox:springfox-spi:2.0.0-SNAPSHOT"
compile "io.springfox:springfox-schema:2.0.0-SNAPSHOT"
compile "io.springfox:springfox-spring-web:2.0.0-SNAPSHOT"
compile "io.springfox:springfox-swagger2:2.0.0-SNAPSHOT"
//Optional for swagger 1.2
compile "io.springfox:springfox-swagger:2.0.0-SNAPSHOT"
```
### Notable Dependencies for 1.0.x
- As of v0.9.5 all dependencies on __scala__ have been removed.
- Spring 3.2.x or above
- jackson 2.4.4
- guava 18.0

## Usage guide and instructions to migrate to 2.0.x 
The support for swagger 2.0 is still in the works. Here is [some preliminary documentation](transitioning-to-v2.md) to 
help early adopters and people brave enough to try it out and provide us with feedback.

## Usage (Quick guide) for 1.0.x
This quick guide outlines how to get swagger-springmvc up and running with a default configuration.
The recommended way to integrate swagger-springmvc with your application is to use the `SwaggerSpringMvcPlugin` as explained below.

#### Spring Java Configuration
- By far, the easiest way to enable swagger
- Assuming you have configured Spring MVC without an xml based servlet application context.
- A typical minimal configuration looks as follows:

```java
@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.myapp.packages")
public class WebAppConfig {
 ...
}
```
The `@EnableSwagger` annotation, in this example, enables swagger-springmvc out of the box. The generated swagger
json Resource Listing is available at /api-docs


#### Spring xml Configuration
- To get the default implementation simply define a bean of type: `springfox.documentation.swagger.configuration.SpringSwaggerConfig`

```xml
<mvc:annotation-driven/> <!-- Required so swagger-springmvc can access spring's RequestMappingHandlerMapping  -->
<bean class="springfox.documentation.swagger.configuration.SpringSwaggerConfig" />
```
- The generated swagger
json Resource Listing is available at /api-docs


### Usage (SwaggerSpringMvcPlugin)

The recommended way to integrate swagger-springmvc with your application is to use the `SwaggerSpringMvcPlugin`. If you are ever
going to need to configure or customize how swagger-springmvc generates your application's swagger api documentation
you are going to need to use the `SwaggerSpringMvcPlugin`.


### SwaggerSpringMvcPlugin XML Configuration
To use the plugin you must create a spring java configuration class which uses spring's `@Configuration`.
This config class must then be defined in your xml application context.


```xml
<!-- Required so swagger-springmvc can access spring's RequestMappingHandlerMapping  -->
<mvc:annotation-driven/>

<bean class="com.yourapp.configuration.MySwaggerConfig"/>
```

```java

//@Configuration
@EnableSwagger //Loads the spring beans required by the framework
public class MySwaggerConfig {

   private SpringSwaggerConfig springSwaggerConfig;

   /**
    * Required to autowire SpringSwaggerConfig
    */
   @Autowired
   public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
      this.springSwaggerConfig = springSwaggerConfig;
   }

   /**
    * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
    * swagger groups i.e. same code base multiple swagger resource listings.
    */
   @Bean
   public SwaggerSpringMvcPlugin customImplementation(){
      return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
              .includePatterns(".*pet.*");
   }

}
```


### SwaggerSpringMvcPlugin Spring Java Configuration
- Use the `@EnableSwagger` annotation.
- Autowire `SpringSwaggerConfig`.
- Define one or more SwaggerSpringMvcPlugin instances using springs `@Bean` annotation.

```java
@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.myapp.controllers")
public class CustomJavaPluginConfig {

   private SpringSwaggerConfig springSwaggerConfig;

   @Autowired
   public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
      this.springSwaggerConfig = springSwaggerConfig;
   }

   @Bean //Don't forget the @Bean annotation
   public SwaggerSpringMvcPlugin customImplementation(){
      return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
            .apiInfo(apiInfo())
            .includePatterns(".*pet.*");
   }

    private ApiInfo apiInfo() {
      ApiInfo apiInfo = new ApiInfo(
              "My Apps API Title",
              "My Apps API Description",
              "My Apps API terms of service",
              "My Apps API Contact Email",
              "My Apps API Licence Type",
              "My Apps API License URL"
        );
      return apiInfo;
    }
}
```


### Swagger-UI

#### Option 1
- __Note:__ Only use this option if you don't need to customize any of the swagger-ui static content, otherwise use option 2.
- Use the web-jar which packages all of the swagger-ui static content.
- Requires that your app is using the servlet 3 specification.
- For non-spring boot applications some extra spring configuration (ResourceHandler's) is required. See: https://github.com/adrianbk/swagger-springmvc-demo/tree/master/swagger-ui

```groovy

dependencies {
  ...
  compile "org.ajar:swagger-spring-mvc-ui:0.4"
}
```

#### Option 2
- Manually copy all of the static content swagger-ui's dist directory (https://github.com/swagger-api/swagger-ui/tree/master/dist)
- Provide the necessary view resolvers and resource handlers to serve the static content.
- Consult the spring documentation on serving static resources.

The following is one way to serve static content from /src/main/webapp
```
<!-- Direct static mappings -->
<mvc:resources mapping="*.html" location="/"/>

<!-- Serve static content-->
<mvc:default-servlet-handler/>
```

## Change log available [here](History.md)

### Migration From 0.8.0 -> 0.8.4+
Prior to 0.8.4 the configuration of Swagger-springmvc was far too verbose as indicated by a number of users. SwaggerSpringMvcPlugin
was introduced to make configuration simpler and less verbose. It is recommended to follow the usage guides above and migrate
your swagger-springmvc configuration to use the `SwaggerSpringMvcPlugin`


### How It works

Swagger-springmvc bootstraps your spring application and scans the `RequestMappingHandlerMapping's` created
by spring to generate the swagger documentation for your applications API's.
Swagger-springmvc stores the generated swagger documentation, in memory, and serves it as JSON using a spring controller.


### Core Concepts

![alt tag](https://raw.githubusercontent.com/martypitt/swagger-springmvc/master/docs/swaggerSpringMvc.png)

#### Swagger group

A swagger group is a concept introduced by this library which is simply a unique identifier for a Swagger Resource Listing
within your application. The reason this concept was introduced was to support applications which require more than one
Resource Listing. Why would you need more than one Resource Listing?
 - A single Spring Web MVC  application serves more than one API e.g. publicly facing and internally facing.
 - A single Spring Web MVC  application serves multiple versions of the same API. e.g. v1 and v2

 In most cases an application will not need more than one Resource Listing and the concept of swagger groups can be ignored.

#### Resource Listing

Please see the Swagger Specification for a detailed explanation.


#### API Documentation Endpoints

All swagger documentation (JSON responses) are served from DefaultSwaggerController. The controller maintains a cache
of ResourcesListing's which are uniquely identified by the `swaggerGroup`. There is a 1:1 relationship between
ResourceListings and swagger groups (`SwaggerSpringMvcPlugin` instances). A typical application will have a single
SwaggerSpringMvcPlugin which is given the unique identifier 'default'.

__Note:__ The below paths are relative to your applications context path and/or DispatcherServlet `url-pattern`


| Path                    | Description                                                             |
|---                      |---                                                                      |
| /api-docs               | Returns the first _Resource Listing_ found in the cache                 |
| /api-docs?group=default | Returns the _Resource Listing_ for the default swagger group            |
| /api-docs?group=group1  | Returns the _Resource Listing_ for the swagger group 'group1'           |
| /api-docs/group1/albums | Returns the album's _Api Declaration_ for the swagger group 'group1'    |


### Urls (SwaggerPathProvider)
The swagger specification recommends the use of absolute URL's where possible - specifically the the `path` attribute of
api's within the ResourceListing's and the `basePath` attribute of Api Declarations. Most users of swagger-springmvc have expressed
a preference for relative urls hence `RelativeSwaggerPathProvider` is the default `SwaggerPathProvider`. `AbsoluteSwaggerPathProvider`
can be used to provide absolute urls. `AbsoluteSwaggerPathProvider` has a hardcoded appRoot but demonstrates the concept. If you wish
to use absolute urls use `AbsoluteSwaggerPathProvider` as a guide and configure your `SwaggerSpringMvcPlugin` with:

 ```java
.pathProvider(myPathProvider)
 ```

### Customization

#### Excluding api endpoints
Annotate a controller class or controller methods with the `@ApiIgnore` annotation.

For more powerful control, specify regular expressions:

```java
swaggerSpringMvcPlugin.includePatterns(...)
```

Exclude all controllers or controller handler methods with specific annotations .
```java
swaggerSpringMvcPlugin.excludeAnnotations(MyCustomApiExclusion.class)

```

#### HTTP Response codes and messages
Configuring global response messages for RequestMappings
```java
swaggerSpringMvcPlugin.globalResponseMessage(new ResponseMessage(OK.value(), "200 means all good \o/", toOption(null)))
```

Configuring per-RequestMappings method response messages
```java
@ApiResponses(value = {@ApiResponse(code = 405, message = "Invalid input")})
public .... createSomething(..)

```

#### Ordering the api's within a ResourceListing
- Defaults to `ResourceListingLexicographicalOrdering`

```java
swaggerSpringMvcPlugin.apiListingReferenceOrdering(new ResourceListingPositionalOrdering())
```

- Use the position attribute of the `@Api` annotation
```
@Controller
@Api(value="", description="Operations on Businesses", position = 2)
public class BusinessService {
    ...
}
```

#### Ordering operations in Api Declarations
Use the swagger `ApiOperation` annotation.
 ```java
   @ApiOperation(value = "", position = 5)
   @RequestMapping("/somewhere")
   public Model methodWithPosition() {
        ...
   }

 ```

#### Ordering ApiDescriptions (withing ApiListing's)
- Defaults to `ApiDescriptionLexicographicalOrdering`

```java
swaggerSpringMvcPlugin.apiDescriptionOrdering(new MyCustomApiDescriptionOrdering());
```

#### Changing how Generic Types are Named

By default, types with generics will be labeled with '\u00ab'(<<), '\u00bb'(>>), and commas. This can be problematic
with things like swagger-codegen. You can override this behavior by implementing your own `GenericTypeNamingStrategy`.
For example, if you wanted `List<String>` to be encoded as 'ListOfString' and `Map<String, Object>`
to be encoded as 'MapOfStringAndObject' you could implement the following:

```java

public class SimpleGenericNamingStrategy implements GenericTypeNamingStrategy {
    private final static String OPEN = "Of";
    private final static String CLOSE = "";
    private final static String DELIM = "And";

    @Override
    public String getOpenGeneric() {
        return OPEN;
    }

    @Override
    public String getCloseGeneric() {
        return CLOSE;
    }

    @Override
    public String getTypeListDelimiter() {
        return DELIM;
    }

}
```

then during plugin customization:

```java
swaggerSpringMvcPlugin.setGenericTypeNamingStrategy(new SimpleGenericTypeNamingStrategy());
```

### Model Customization
#### Excluding spring handler method arguments or custom types
To exclude controller method arguments form the generated swagger model JSON.
```java
swaggerSpringMvcPlugin.ignoredParameterTypes(MyCustomType.class)
```
By default, a number of Spring's handler method arguments are ignored. See: springfox.documentation.swagger.configuration.SpringSwaggerConfig#defaultIgnorableParameterTypes


##Development

- Development environment and build tasks See: [build.md] (https://github.com/martypitt/swagger-springmvc/blob/master/build.md)
- [Release process](https://github.com/martypitt/swagger-springmvc/issues/422)
- Contributing - please see the [wiki](https://github.com/martypitt/swagger-springmvc/wiki) for some guidelines

## Support

If you find issues or bugs please use the github issue [tracker] (https://github.com/martypitt/swagger-springmvc/issues)

License
-------

Copyright 2015 Marty Pitt - [@martypitt](https://github.com/martypitt), Dilip Krishnan - [@dilipkrish](https://github.com/dilipkrish),
Adrian Kelly -  [@adrianbk](https://github.com/adrianbk),

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

