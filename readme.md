# Swagger / Spring MVC Integration

[![Build Status](https://travis-ci.org/adrianbk/swagger-springmvc.png?branch=swagger-spec-1.2.0-upgrade)](https://travis-ci.org/adrianbk/swagger-springmvc)

Sample Project: [here](https://github.com/adrianbk/swagger-springmvc-demo)

WIP. Model/Model schema not yet implemented..

An upgrade to the swagger-springmvc project to the latest Swagger specification (1.2)
The swagger specification upgrade to 1.2 has several feature additions and has also refactored it's core model structure.
See [swagger-core](https://github.com/wordnik/swagger-core)
The [demo/sample](https://github.com/adrianbk/swagger-springmvc-demo) application is built off of swagger-ui 4th Noc 2013 (SHA: 4601f6270235489581acd80548620716506311a5)

Swagger 1.2 supported features:
- Authorization types: (OAuth, ApiKey, BasicAuth) (swagger-ui has not yet upgraded to support these)
- ApiInfo: info, title, licencing, etc.
- Http media types (produces/consumes)
- resource & base path changes
For more detail see: https://github.com/wordnik/swagger-core/wiki/1.2-transition


## Features/changes
- Supports multiple instances of swagger api resource listings from the same spring mvc application
- Authorization types.
- HTTP media types
- Request mappings with regex expressions do not error out
- All http methods supported by org.springframework.web.bind.annotation.RequestMethod (GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE)
- Filtering/Inclusion of api endpoints with regular expression or ant path matching.
- Operation parameter data types supported as per spec: https://github.com/wordnik/swagger-core/wiki/Datatypes
- All uris are, by default, absolute after much deliberation. Relative uri's do not work well with swagger UI. Other swagger tools like
swagger-codegen work much better with absolute uri's.
This strategy can be changed by
implementing [SwaggerPathProvider](https://github.com/adrianbk/swagger-springmvc/blob/swagger-spec-1.2.0-upgrade/src/main/java/com/mangofactory/swagger/core/SwaggerPathProvider.java)

## Dependencies
- Spring 3.1.1 or above (due to a bug in UriComponentsBuilder)
- scala lib 2.9.1-1
- jackson 2.2.3 (older versions may work)

##Adding to to a spring MVC application

web application context xml config

```xml
<mvc:annotation-driven>
<context:annotation-config/>
<context:component-scan base-package="com.ak.swaggermvc.demo"/>

<!-- Enable the default documentation controller-->
<context:component-scan base-package="com.mangofactory.swagger.controllers"/>

<!-- Pick up the bundled spring config-->
<context:component-scan base-package="com.mangofactory.swagger.configuration"/>
```

Java spring config
```java

   @Autowired
   private SpringSwaggerConfig springSwaggerConfig;

   /**
    * Adds the jackson scala module to the MappingJackson2HttpMessageConverter registered with spring
    * Swagger  core models are scala so we need to be able to convert to JSON
    */
   @Bean
   public JacksonScalaSupport jacksonScalaSupport(){
      JacksonScalaSupport jacksonScalaSupport = new JacksonScalaSupport();
      //Set to false to disable
      jacksonScalaSupport.setRegisterScalaModule(true);
      return jacksonScalaSupport;
   }

   @Bean
   @Autowired
   public SwaggerApiResourceListing swaggerApiResourceListing() {
      SwaggerApiResourceListing swaggerApiResourceListing = new SwaggerApiResourceListing(springSwaggerConfig.swaggerCache(), "business-api");
      swaggerApiResourceListing.setSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
      swaggerApiResourceListing.setApiInfo(apiInfo());
      swaggerApiResourceListing.setAuthorizationTypes(authorizationTypes());
      swaggerApiResourceListing.setIgnorableParameterTypes(springSwaggerConfig.defaultIgnorableParameterTypes());

      ApiListingReferenceScanner apiListingReferenceScanner = apiListingReferenceScanner();
      swaggerApiResourceListing.setApiListingReferenceScanner(apiListingReferenceScanner);
      return swaggerApiResourceListing;
   }

   @Bean
   public ApiListingReferenceScanner apiListingReferenceScanner() {
      ApiListingReferenceScanner apiListingReferenceScanner = new ApiListingReferenceScanner();
      apiListingReferenceScanner.setRequestMappingHandlerMapping(springSwaggerConfig.swaggerRequestMappingHandlerMappings());
      apiListingReferenceScanner.setExcludeAnnotations(springSwaggerConfig.defaultExcludeAnnotations());
      apiListingReferenceScanner.setControllerNamingStrategy(springSwaggerConfig.defaultControllerResourceNamingStrategy());
      apiListingReferenceScanner.setSwaggerPathProvider(springSwaggerConfig.defaultSwaggerPathProvider());
      //Must match the swagger group set on SwaggerApiResourceListing
      apiListingReferenceScanner.setSwaggerGroup("business-api");
      //Only add the businesses endpoints to this api listing
      apiListingReferenceScanner.setIncludePatterns(
            Arrays.asList(new String[]{
                  "/business.*"
            })
      );
      return apiListingReferenceScanner;
   }

   private List<AuthorizationType> authorizationTypes() {
      ArrayList<AuthorizationType> authorizationTypes = new ArrayList<AuthorizationType>();
      authorizationTypes.add(new ApiKey("x-auth-token", "header"));
      return authorizationTypes;
   }

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

Copyright 2012 Marty Pitt - [@martypitt](https://github.com/martypitt), Dilip Krishnan - [@dilipkrish](https://github.com/dilipkrish)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

