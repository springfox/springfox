### About

This project integrates swagger with the Spring Web MVC framework. The complete swagger specification is available
at https://github.com/wordnik/swagger-spec and it's worth being familiar with the main concepts of the specification.

Typically a Spring Web MVC project will use this project in combination with the swagger-ui project to provide the user interface which visualises an applications JSON api's. The most common know use of this project has been Spring Web MVC applications using springs `MappingJackson2HttpMessageConverter` to produce JSON API endpoints.

The demo project (https://github.com/adrianbk/swagger-springmvc-demo) containes a number of examples using both spring web mvc and spring-boot.


### Usage (Quick guide)

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
    <version>0.8.4</version>
  </dependency>
```

- Gradle
```groovy
compile "com.mangofactory:swagger-springmvc:0.8.4"
```

###### Snapshot version
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

#### Spring Java Configuration
- By far, the easiest way to enable swagger
- A typical minimal configuration looks as follows:

```
@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.myapp.packages")
public class WebAppConfig {
 ...
}
```
The `@EnableSwagger` annotation, in this example, enbles swagger-springnvc out of the box and the swagger json Resource Listing is available at /api-docs


#### Spring xml Configuration
- To get the default implementation simply define a bean of type: `com.mangofactory.swagger.configuration.SpringSwaggerConfig`

```xml
<mvc:annotation-driven/> <!-- Required so swagger-springmvc can access spring's RequestMappingHandlerMapping  -->
<bean class="com.mangofactory.swagger.configuration.SpringSwaggerConfig" />
```
- JSON Resource Listing availagle at /api-docs

### Customization

sd
#### SwaggerSpringMvcPlugin

sd


### Migration From 0.8.0 -> 0.8.4


### How It works

Swagger-springmvc bootstraps your spring application and scans the `RequestMappingHandlerMapping's` created
by spring to generate the swagger documentation for your applications API's. Swagger-springmvc depends on
the swagger-core library which is actively maintained by the creators of the swagger specification.
Swagger-springmvc is written in Java and Swagger core is written in Scala (swagger-springmvc is tested using groovy).
Swagger-core is effectively the base implementation of the Swagger specification, it defines all models and
annotations as mentioned in the swagger specification.
Swagger-springmvc stores the generated swagger documentation, in memory, and serves it as JSON using a spring controller.


### Core Concepts

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

