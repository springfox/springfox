# SpringFox Reload

项目基于[springfox/springfox: Automated JSON API documentation for API's built with Spring (github.com)](https://github.com/springfox/springfox)进行的改动

The project is based on the changes made by [springfox/springfox: Automated JSON API documentation for API's built with Spring (github.com)](https://github.com/springfox/springfox)

因为在SpringBoot3中直接使用springfox来启用文档会导致报错从而无法进行正常的使用，其原因是SpringBoot3中有一些Class被执行了重构，比如原本调用的Enum常量变成了String常量等等。

Because using springfox directly in SpringBoot3 to enable documents will result in an error and cannot be used normally. The reason is that some Classes in SpringBoot3 have been refactored, such as the originally called Enum constants become String constants and so on.

您可以直接通过引用我的私人Repository来引用这份重构之后的SpringFox，这个源同时包含了springplugin、mavencentral、google、gradle、milestone等源的代理镜像

You can directly refer to this refactored SpringFox by citing my private Repository. This source also includes proxy images of sources such as springplugin, mavencentral, google, gradle, and milestone

## 使用方法/Usage

**Maven使用方法/Usage for maven**

在你的项目中添加该节点

Add this repository in your project

```xml
<repositories>
   <repository>
      <id>xssoft</id>
      <url>https://xssoft.club:5678/repository/main</url>
      <releases>
         <enabled>true</enabled>
      </releases>
      <snapshots>
         <enabled>true</enabled>
      </snapshots>
   </repository>
</repositories>
```

在你的maven项目依赖中添加

and add this to your project dependencies

```xml
<dependency>
   <groupId>io.springfox</groupId>
   <artifactId>springfox-boot-starter</artifactId>
   <version>4.0.3</version>
</dependency>
```

并且创建Spring Configuration

and create this Spring Configuration

```java
@Configuration
@EnableOpenApi
public class SpringFoxConfig implements WebMvcConfigurer {
    private final SwaggerProperties swaggerProperties;

    public SpringFoxConfig(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    /**
     * Swagger挂载点设置
     * @return
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                .enable(swaggerProperties.getEnable())
                .apiInfo(apiInfo())
                .host(swaggerProperties.getTryHost())
                .protocols(newHashSet("https", "http"))
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(swaggerProperties.getApplicationName() + " Api Doc")
            .description(swaggerProperties.getApplicationDescription())
            .contact(new Contact("MawManager", null, "mawserver@foxmail.com"))
            .version("程序版本号: " + swaggerProperties.getApplicationVersion() + ", Spring Boot框架版本号: " + SpringBootVersion.getVersion())
            .build();
    }

    /**
     * 设置授权信息
     */
    private List<SecurityScheme> securitySchemes() {
        ApiKey apiKey = new ApiKey("BASE_TOKEN", "token", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(Collections.singletonList(new SecurityReference("BASE_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
                        .build()
        );
    }

    /**
     * 新的哈希数组
     * @param ts 参数
     * @return 新的哈希数组
     * @param <T> 类型
     */
    @SafeVarargs
    private <T> Set<T> newHashSet(T... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/").resourceChain(false);
        registry.addResourceHandler("/documentation/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/documentation/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui").setViewName("forward:/swagger-ui/index.html");
        registry.addRedirectViewController("/documentation/v3/api-docs", "/v3/api-docs?group=restful-api");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/ui", "/swagger-resources/configuration/ui");
        registry.addRedirectViewController("/documentation/swagger-resources/configuration/security", "/swagger-resources/configuration/security");
        registry.addRedirectViewController("/documentation/swagger-resources", "/swagger-resources");
    }
}
```

现在你可以在/swagger-ui/index.html访问你的OAS_30文档了

Now you can access your OAS_30 documentation at /swagger-ui/index.html

 **mawserver@foxmail.com 连旭灿进行的修改，如果你有各种软件需求欢迎联系**

