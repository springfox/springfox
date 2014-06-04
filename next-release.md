# Swagger-springmvc

Unit Tests: [![Build Status](https://travis-ci.org/martypitt/swagger-springmvc.png?branch=master)](https://travis-ci.org/martypitt/swagger-springmvc)

Functional Tests: [![Build Status](https://travis-ci.org/adrianbk/swagger-springmvc-demo.png?branch=master)](https://travis-ci.org/adrianbk/swagger-springmvc-demo)

### About

This project integrates swagger with the Spring Web MVC framework. The complete swagger specification is available
at https://github.com/wordnik/swagger-spec and it's worth being familiar with the main concepts of the specification.

Typically a Spring Web MVC project will use this project in combination with the swagger-ui project (https://github.com/wordnik/swagger-ui) 
to provide the user interface which visualises an applications JSON api's. The most common know use of this project has been 
Spring Web MVC applications using springs `MappingJackson2HttpMessageConverter` to produce JSON API endpoints.

The demo project (https://github.com/adrianbk/swagger-springmvc-demo) containes a number of examples using both spring web mvc and spring-boot.

### Repositories

#### Release version
or maven central: http://repo1.maven.org/maven2/

- Maven
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
    <version>0.8.4</version>
  </dependency>
```

- Gradle
```groovy
compile "com.mangofactory:swagger-springmvc:0.8.4"
```

#### Snapshot version
- Maven
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

- Gradle

```groovy
compile "com.mangofactory:swagger-springmvc:0.8.4-SNAPSHOT"
```

### Usage (Quick guide)
This quick guide outlines how to get swagger-springmvc up and running with a default configuration. The recommended way to integrate swagger-springmvc with your application is to use the `SwaggerSpringMvcPlugin` explained in the [Usage (SwaggerSpringMvcPlugin)][] section.

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
The `@EnableSwagger` annotation, in this example, enbles swagger-springnvc out of the box and the generated swagger json Resource Listing is available at /api-docs


#### Spring xml Configuration
- To get the default implementation simply define a bean of type: `com.mangofactory.swagger.configuration.SpringSwaggerConfig`

```xml
<mvc:annotation-driven/> <!-- Required so swagger-springmvc can access spring's RequestMappingHandlerMapping  -->
<bean class="com.mangofactory.swagger.configuration.SpringSwaggerConfig" />
```
- JSON Resource Listing available at /api-docs



### Usage (SwaggerSpringMvcPlugin)

The recommended way to integrate swagger-springmvc with your application is to use the `SwaggerSpringMvcPlugin`. If you are ever 
going to need to configure or customize how swagger-springmvc generates your applicatios sagger api documentation 
you are going to need to use the `SwaggerSpringMvcPlugin`.


### SwaggerSpringMvcPlugin XML Configuration
To use the plugin you must create a spring java configuration class which uses spring's `@Configuration`.
This config class must then be defined in your xml application context. 


```xml
<mvc:annotation-driven/> <!-- Required so swagger-springmvc can access spring's RequestMappingHandlerMapping  -->
<bean class="com.yourapp.configuration.MySwaggerConfig"/>
```



```java

@Configuration
@EnableSwagger //Loads the spring beans required by the framework most of which are available in SpringSwaggerConfig 
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
- Define one of more SwaggerSpringMvcPlugin using springs `@Bean` annotation.

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

   @Bean
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
- For non-spring boot applications some extra spring configuration is required. See: https://github.com/adrianbk/swagger-springmvc-demo/tree/master/swagger-ui
```
dependencies {

  ...
  compile "org.ajar:swagger-spring-mvc-ui:0.2"

}
```

#### Option 2
- Manually copy all of the static content swagger-ui's dist directory (https://github.com/wordnik/swagger-ui/tree/master/dist)
- Provide the necessary view resolvers and resource handlers to serve the static content.
- Consult the spring documentation on serving static resources.

The following is one way to serve static content from /src/main/webapp
```
    <!-- Direct static mappings -->
    <mvc:resources mapping="*.html" location="/"/>
    
    <!-- Serve static content-->
    <mvc:default-servlet-handler/>
```

### Migration From 0.8.0 -> 0.8.4
Prior to 0.8.4 the configuration of Swagger-springmvc was far too verbose as indicated by a number of users. SwaggerSpringMvcPlugin 
was introduced to make configuration simpler and less verbose. It is recommended to follow the usage guides above and migrate 
your swagger-springmvc configuration to use the `SwaggerSpringMvcPlugin`


### How It works

Swagger-springmvc bootstraps your spring application and scans the `RequestMappingHandlerMapping's` created
by spring to generate the swagger documentation for your applications API's. Swagger-springmvc depends on
the swagger-core library which is actively maintained by the creators of the swagger specification.
Swagger-springmvc is written in Java and Swagger core is written in Scala (swagger-springmvc is tested using groovy).
Swagger-core is effectively the base implementation of the Swagger specification, it defines all models and
annotations as mentioned in the swagger specification.
Swagger-springmvc stores the generated swagger documentation, in memory, and serves it as JSON using a spring controller.


### Core Concepts

![alt tag](https://raw.githubusercontent.com/martypitt/swagger-springmvc/master/docs/swaggerSpringMvc.png)

1. Swagger group
A swagger group is a concept introduced by this library which is simply a unique identifier for a Swagger Resource Listing
within your application. The reason this concept was introduced was to support applications which require more than one
Resource Listing. Why would you need more than one Resource Listing?
 - A single Spring Web MVC  application serves more than one API e.g. publicly facing and internally facing.
 -  A single Spring Web MVC  application serves multiple versions of the same API. e.g. v1 and v2

 In most cases an application will not need more than one Resource Listing and the concept of swagger groups can be ignored.

2. Resource Listing
Please see the Swagger Specification for a detailed explanation.



### Notable Dependencies

### Urls (SwaggerPathProvider)
The swagger specification recommends the use of absolute URL's for where possible - specifically the the `path` attribute of 
api's within the ResourceListing's and the `basePath` attribute of Api Declarations. Most users of swagger-springmvc have expressed 
a preference for relative urls hence `RelativeSwaggerPathProvider` is the default SwaggerPathProvider`. `AbsoluteSwaggerPathProvider` can be
used to provide absolute urls. `AbsoluteSwaggerPathProvider`has a hardcoded appRoot but demonstrates the concept. If you wish use absolute urls:
use `AbsoluteSwaggerPathProvider` as a guide and configure your `SwaggerSpringMvcPlugin` with: 
 
 ```java
.pathProvider(myPathProvider) 
 ```


### Customization

#### Ordering the api's within a ResourceListing

```java

//If not supplied the default is ResourceListingLexicographicalOrdering
swaggerSpringMvcPlugin.apiListingReferenceOrdering(new ResourceListingPositionalOrdering())
```