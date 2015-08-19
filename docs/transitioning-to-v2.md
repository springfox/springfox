### Module changes

Also what used to be two modules ```swagger-springmvc``` and ```swagger-models``` 
is now split into multiple modules. 

A little bit of background; when we started work on 2.0 swagger specification we realized that we're rewriting the logic to infer the service
 models and the schema. So we decided to take a step back and break it out into a two step process. First infer the service 
 model into an internal representation. Second create a mapping layer that can map the internal models to different specification formats. 
 Out of the box we will support swagger 1.2 and swagger 2.0, but this leads us to the possibility of supporting other formats and 
 other scenarios as well; for e.g. RAML, ALPS and hypermedia formats.
 
Accordingly the different modules are split up as shown below.

```ascii
                                                                                                                               
                                   +------------------+                                                                 
                                   |                  |        Contains the internal service and                        
                                   |  springfox-core  |        schema description models along with                     
                                   |                  |        their builders.                                          
                                   +---------+--------+                                                                 
                                             ^                                                                          
                                   +---------+--------+                                                                 
                                   |                  |        Contains the service provider interfaces that            
                                   |  springfox-spi   |        can be used to extend and enrich the service models.     
                                   |                  |        For e.g. swagger specific annotation processors.         
                                   +---+------+----+--+                                                                 
                                       ^      ^    ^                                                                    
                       +---------------+----+ | +--+------------------+                                                 
Schema inference       |                    | | |                     | spring web specific extensions that can build
extensions that help   |  springfox-schema  | | |springfox-spring-web | the service models based on RequestMapping   
build up the schema for|                    | | |                     | information. This is the heart library that  
the parameters, models +--------------------+ | +---------------------+ infers the service model.                    
and responses                                 |                                                                         
                                 +------------+-------------+                                                           
                                 |                          |   Common swagger specific extensions                      
                                 | springfox-swagger-common |   that are aware of the different                         
                                 |                          |   swagger annotations.                                    
                                 +-----+---------------+----+                                                           
                                       ^               ^                                                                
                         +-------------+----+     +----+--------------+                                                 
                         |                  |     |                   |  Configurations, and mapping layer              
                         |springfox-swagger1|     |springfox-swagger2 |  that know how to convert the                   
                         |                  |     |                   |  service models to swagger 1.2 and              
                         +------------------+     +-------------------+  swagger 2.0 specification documents.  A
                                                                         Also contains the controller for each
                                                                         of the specific formats.

```

### Configuration changes

Firstly all the package names have changed

```java

package com.mangofactory.swagger.*;

//is now

package springfox.documentation.*;

```

To enable support for swagger specification 1.2 use the ```@EnableSwagger``` annotation

To enable support for swagger specification 2.0 use the ```@EnableSwagger2``` annotation

We no longer use ```SwaggerSpringMvcPlugin``` to configure the documentation subset. This has been replaced by a 
more generic ```Docket``` class. This is changed to be more inline with the fact that expressing the
contents of the documentation is agnostic of the format the documentation is rendered. Also we no longer need the 
following class ```SpringSwaggerConfig``` in order to configure the ```Docket```.

```java
  // This configuration is no longer used
  @Autowired
  private SpringSwaggerConfig swaggerConfig;
```

Docket [stands for](https://www.wordnik.com/words/docket) *A summary or other brief statement of the contents of a 
document; an abstract.*

```Docket``` is very similar to ```SwaggerSpringMvcPlugin```, in that, it helps configure a 
subset of the services to be documented and groups them by name. Significant changes to this is the ability
to provide an expressive predicate based for api selection.

```java
  import static springfox.documentation.builders.PathSelectors.regex;
  import static com.google.common.base.Predicates.or;

  @Bean
  public Docket swaggerSpringMvcPlugin() {
    return new Docket(DocumentationType.SWAGGER_2)
            .groupName("business-api")
            .select() 
               //Ignores controllers annotated with @CustomIgnore
              .apis(not(withClassAnnotation(CustomIgnore.class)) //Selection by RequestHandler
              .paths(paths()) // and by paths
              .build()
            .apiInfo(apiInfo())
            .securitySchemes(securitySchemes())
            .securityContext(securityContext());
  }

  //Here is an example where we select any api that matches one of these paths
  private Predicate<String> paths() {
    return or(
        regex("/business.*"),
        regex("/some.*"),
        regex("/contacts.*"),
        regex("/pet.*"),
        regex("/springsRestController.*"),
        regex("/test.*"));
  }

```

For a list of handy predicates Look at [RequestHandlerSelectors](https://github.com/springfox/springfox/blob/master/springfox-core/src/main/java/springfox/documentation/builders/RequestHandlerSelectors.java)
and [PathSelectors](https://github.com/springfox/springfox/blob/master/springfox-core/src/main/java/springfox/documentation/builders/PathSelectors.java).

### Configuring the ObjectMapper 
A simple way to configure the object mapper is to listen for the ```ObjectMapperConfigured``` event. Regardless of 
whether there is a customized ObjectMapper in play with a corresponding MappingJackson2HttpMessageConverter, the 
library always has a configured ObjectMapper that is customized to serialize swagger 1.2 and swagger 2.0 types. 

In order to do this implement the ```ApplicationListener<ObjectMapperConfigured>``` interface. The event has a handle
 to the ObjectMapper that was configured. Configuring application specific ObjectMapper customizations in this 
 application event handler guarantees that application specific  customizations will be applied to each and every 
 ObjectMapper that is in play.
 
If you encounter a NullPointerException during application startup like [this issue](https://github
.com/springfox/springfox/issues/635). Its because most likely the ```WebMvcConfigurerAdapter``` isn't working. 
These adapter especially in a non-spring-boot scenarios will only get loaded if the @EnableWebMvc [annotation is 
present](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/config/annotation
/WebMvcConfigurer.html).

### Customizing the swagger endpoints.
By default the swagger service descriptions are generated at the following urls

Swagger version  | Documentation Url
---------------- | -----------------
1.2              | /api-docs
2.0              | /v2/api-docs

To customize these endpoints, loading a [property source](http://docs.spring
.io/spring/docs/current/javadoc-api/org/springframework/context/annotation/PropertySource.html) with the following properties 
allows the properties to be  overridden

Swagger version | Override property
--------------- | -----------------
1.2             | springfox.documentation.swagger.v1.path
2.0             | springfox.documentation.swagger.v2.path

### Overriding property datatypes
Using the ```ApiModelProperty#dataType``` we can override the inferred data types. However it is restricted
 to only allow data types to be specified with a fully qualified class name. For e.g. if we have the following 
 definition
 
 ```java
    
    // if com.qualified.ReplaceWith is not a Class that can be created using Class.forName(...) 
    // Original will be replaced with the new class 
    @ApiModelProperty(dataType = "com.qualified.ReplacedWith")
    public Original getOriginal() { ... }
    
    // if ReplaceWith is not a Class that can be created using Class.forName(...) Original will be preserved
    @ApiModelProperty(dataType = "ReplaceWith")
    public Original getAnotherOriginal() { ... }
 ```

### Extensibility 
The library provides a variety of extensibility hooks to enrich/ augment the schema and service models

- For enriching models and properties 
####TODO
- For enriching services models 
####TODO

### Example application
For an example for porting a 1.0.2 application to a 2.0.x application (in progress) take a look [at this branch](https://github.com/adrianbk/swagger-springmvc-demo/tree/feature/2.0-showcase) or [the spring boot example](https://github.com/springfox/springfox-demos)  in the demo application.


