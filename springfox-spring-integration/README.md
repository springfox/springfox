# Springfox Spring-Integration

Common parts of spring-integration WebFlux and WebMvc implementations.

## Choose Your Web Technology

In Spring Integration, the spring-integration-http module contains common base classes for WebFlux and WebMvc. 
In your Spring Integration application, you need to choose the actual web technology by defining the correct 
dependency to WebMvc or WebFlux, e.g. by depending on spring-boot-starter-web or spring-boot-starter-webflux 
respectively. This is described in the spring-integration reference guide. Depending on the web technology you have to
choose the matching springfox-spring-integration implementation for WebMvc _or_ WebFlux - you cannot have both.

## Document Using Tests

Furthermore, since it is not feasible to use  static code analysis to find out about responses of spring-integration 
http inbound endpoints, springfox-spring-integration offers a possibility to use spring-restdocs to document response
bodies. We inject our own `SpringFoxTemplateFormat` instead of asciidoc or markdown in the test configuration 
and evaluate the documentation snippets generated during test execution from this template format in 
`SpringRestDocsOperationBuilderPlugin`.

For more information see the readme files of 
[springfox-spring-integration-webmvc](../springfox-spring-integration-webmvc) and
 [springfox-spring-integration-webflux](../springfox-spring-integration-webflux).  

