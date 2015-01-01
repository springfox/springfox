package com.mangofactory.swagger.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangofactory.service.model.ApiInfo;
import com.mangofactory.service.model.builder.ApiInfoBuilder;
import com.mangofactory.swagger.controllers.Defaults;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("com.mangofactory.swagger.dummy") //Scan some controllers
public class CustomJavaPluginConfig {

  @Autowired
  private Defaults defaults;

  /**
   * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
   * swagger groups i.e. same code base multiple swagger resource listings
   */
  @Bean
  public SwaggerSpringMvcPlugin customImplementation() {
    return new SwaggerSpringMvcPlugin(this.defaults)
            .swaggerGroup("customPlugin")
            .includePatterns(".*pet.*");
  }

  @Bean
  public SwaggerSpringMvcPlugin secondCustomImplementation() {
    return new SwaggerSpringMvcPlugin(this.defaults)
            .swaggerGroup("secondCustomPlugin")
            .apiInfo(apiInfo())
            .includePatterns("/feature.*");
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  private ApiInfo apiInfo() {
    ApiInfo apiInfo = new ApiInfoBuilder().title("My Apps API Title").description("My Apps API Description")
            .termsOfServiceUrl("My Apps API terms of service").contact("My Apps API Contact Email").license("My Apps " +
                    "API Licence Type").licenseUrl("My Apps API License URL").build();
    return apiInfo;
  }
}
