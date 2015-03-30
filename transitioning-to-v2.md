Firstly all the package names have changed

```java

package com.mangofactory.swagger.*;

//is now

package springfox.documentation.*;

```

Also what used to be two modules ```swagger-springmvc``` and ```swagger-models``` 
is now split into multiple modules. 

A little bit of background; when we started work on 2.0 swagger specification we
 realized that we're rewriting the logic to infer the service models and the schema.  
So we decided to take a step back and break it out into a two step process. First infer 
the service model into an internal representation. Second create a mapping layer that 
can map the internal models to different specification formats. Out of the box we will 
support swagger 1.2 and swagger 2.0, but this leads us to the possibility of supporting
 other formats and other scenarios as well; for e.g. RAML, ALPS and hypermedia formats.
 
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
Schema inference       |                    | | |                     |    spring web specific extensions that can build
extensions that help   |  springfox-schema  | | |springfox-spring-web |    the service models based on RequestMapping   
build up the schema for|                    | | |                     |    information. This is the heart library that  
the parameters, models +--------------------+ | +---------------------+    infers the service model.                    
and responses                                 |                                                                         
                                 +------------+-------------+                                                           
                                 |                          |   Common swagger specific extensions                      
                                 | springfox-swagger-common |   that are aware of the different                         
                                 |                          |   swagger annotations.                                    
                                 +-----+---------------+----+                                                           
                                       ^               ^                                                                
                         +-------------+----+     +----+--------------+                                                 
                         |                  |     |                   |  Configurations, and mapping layer              
                         |springfox-swagger |     |springfox-swagger2 |  that know how to convert the                   
                         |                  |     |                   |  service models to swagger 1.2 and              
                         +------------------+     +-------------------+  swagger 2.0 specification documents.           


```

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

compile "io.springfox:springfox-core:2.0.0-SNAPSHOT",
compile "io.springfox:springfox-spi:2.0.0-SNAPSHOT",
compile "io.springfox:springfox-schema:2.0.0-SNAPSHOT",
compile "io.springfox:springfox-spring-web:2.0.0-SNAPSHOT",
compile "io.springfox:springfox-swagger2:2.0.0-SNAPSHOT"
//Optional for swagger 1.2
compile "io.springfox:springfox-swagger:2.0.0-SNAPSHOT"
```


To enable support for swagger specification 1.2 use the ```@EnableSwagger``` annotation
To enable support for swagger specification 2.0 use the ```@EnableSwagger2``` annotation
                                                                                            
We no longer use ```SwaggerSpringMvcPlugin``` to configuration the documentation subset. This has 
been replaced by a more generic ```Docket```. class. 

Docket [stands for](https://www.wordnik.com/words/docket) *A summary or other brief statement of the contents of a 
document; an abstract.*

For the most part the ```Docket``` is very similar to ```SwaggerSpringMvcPlugin```, in that it helps configure a 
subset of the services to be documented and groups them by name. Significant changes to this is the ability
to provide a predicate for api selection.


```java

  @Bean
  public Docket swaggerSpringMvcPlugin() {
    return new Docket(DocumentationType.SWAGGER_2)
            .groupName("business-api")
            .select() 
               //Ignores controllers annotated with @CustomIgnore
              .apis(not(withClassAnnotation(CustomIgnore.class))
              .paths(paths())
              .build()
            .apiInfo(apiInfo())
            .authorizationTypes(authorizationTypes())
            .authorizationContext(authorizationContext());
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

For a list of handy predicates Look at [RequestHandlerSelectors](https://github
.com/springfox/springfox/blob/refactor/538/spike-to-seperate-models-and-dtos/springfox-core/src/main/java/springfox/documentation/builders/RequestHandlerSelectors.java)
and [PathSelectors](https://github.com/springfox/springfox/blob/refactor/538/spike-to-seperate-models-and-dtos
/springfox-core/src/main/java/springfox/documentation/builders/PathSelectors.java)


