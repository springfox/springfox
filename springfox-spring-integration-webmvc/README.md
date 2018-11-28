# Springfox Spring-Integration WebMvc

Adds basic automatic recognition of SpringFox documentation for spring-integration WebMvc inbound http endpoints and
allows to document responses through tests.

## Springfox in Spring Integration WebMvc

To enable springfox in your spring-integration webmvc application, add the following dependencies:

```
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-spring-webmvc</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-spring-integration-webmvc</artifactId>
        <version>${springfox.version}</version>
    </dependency>
    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>${springfox.version}</version>
    </dependency>
```

## Using Spring REST Docs with Springfox

In spring-integration there is no way to determine the resulting response by looking at a method return value - the 
response is created further down the line by the integration flow which is attached to an http inbound endpoint. 
The inbound endpoint has no knowledge about the ultimate response.

Therefore we cannot provide response examples automatically, based on static code analysis.

Instead, you can use integration tests to create documentation about the response bodies of an integration flow, 
building on the [spring-restdocs](https://spring.io/projects/spring-restdocs) project.

Add a dependency to `spring-restdocs-mockmvc`:

```
    <dependency>
        <groupId>org.springframework.restdocs</groupId>
        <artifactId>spring-restdocs-mockmvc</artifactId>
        <scope>test</scope>
    </dependency>
```

Define a rule for restdocs in your JUnit MockMvc test:
```
   @Rule
   public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
```

Configure spring-restdocs to use the `SpringfoxTemplateFormat` in your test setup:
```
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
        .apply(documentationConfiguration(this.restDocumentation)
            .snippets()
            .withTemplateFormat(
                new SpringfoxTemplateFormat()) // <-- Template format
        )
        .build();

```

Define the id of the inbound endpoint in the spring-integration Java DSL with a human friendly name:
```
    @Bean
    public IntegrationFlow toLowerFlow() {
        return IntegrationFlows.from(
                Http.inboundGateway("/conversions/lower")
                        .requestMapping(r -> r.methods(HttpMethod.POST)
                                .consumes("application/json"))
                        .requestPayloadType(Foo.class)
                        .id("toLowerGateway"))            // <-- id of endpoint
                .<Foo>handle((p, h) -> new Foo(p.getBar()
                        .toLowerCase()))
                .get();
    }
```

Document your response as part of the MockMvc request, spring-restdocs style. Note the parameter which is 
given to the `document()` method, it must start with the id of the inbound endpoint:

```
    mockMvc.perform(
        MockMvcRequestBuilders.post("/conversions/lower")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"foo\": \"TOLOWERTHIS\"}"))
        .andExpect(MockMvcResultMatchers.status()
            .isOk())
        .andDo(document("toLowerGatewaySuccess")        // <-- starts with id of endpoint
        )));
```
This allows Springfox to collect all spring-restdocs snippets which belong to a particular endpoint.

Note that the OpenApi 2.0 Specification allows only [one example per response code and media-type](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#responseObject). 
Starting from OpenApi 3.0 this will [change](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md#media-type-object), but Springfox does not support that version yet.
Therefore, if you have more than one test with a response having the same status code and media-type, Springfox's 
internal data structure does maintain all examples correctly, but the Swagger2 documentation will only 
use the last example.


Spring-Restdocs records request and response snippets during test execution, by default to the 
folder _target/generated-snippets_.

You need to tell the build that you want these snippets included in the application jar.

In Maven:

```
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-resources-plugin</artifactId>
        <version>3.1.0</version>
        <executions>
            <execution>
                <id>add-restdocs</id>
                <phase>prepare-package</phase>
                <goals>
                    <goal>copy-resources</goal>
                </goals>
                <configuration>
                    <outputDirectory>${project.build.directory}/classes</outputDirectory>
                    <resources>
                        <resource>
                            <directory>target/generated-snippets</directory>
                        </resource>
                    </resources>
                </configuration>
            </execution>
        </executions>
    </plugin>
``` 

In Gradle we also need to ensure that the 'test' task is executed before 'jar':

```
jar {
    dependsOn 'test'
    sourceSets {
        main {
            java {
                srcDirs = ['src/main/java']
            }
            resources {
                srcDirs = ["target/generated-snippets", "src/main/resources"]
            }
        }
    }
}
```
## Examples

You can find an example application at 
[springfox-integration-webmvc](https://github.com/springfox/springfox-demos/springfox-integration-webmvc).


