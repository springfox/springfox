/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger1.annotations.EnableSwagger;

import static springfox.documentation.spi.DocumentationType.*;


@Configuration
@EnableWebMvc
@EnableSwagger
@ComponentScan("springfox.documentation.spring.web.dummy") //Scan some controllers
public class CustomJavaPluginConfig {

  /**
   * Every SwaggerSpringMvcPlugin bean is picked up by the swagger-mvc framework - allowing for multiple
   * swagger groups i.e. same code base multiple swagger resource listings
   */
  @Bean
  public Docket customImplementation() {
    return new Docket(SWAGGER_12)
        .groupName("customPlugin")
        .select()
          .paths(PathSelectors.regex(".*pet.*"))
          .build();
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(SWAGGER_12)
        .groupName("secondCustomPlugin")
        .apiInfo(apiInfo())
        .select()
          .paths(PathSelectors.regex("/feature.*"))
          .build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("My Apps API Title")
        .description("My Apps API Description")
        .termsOfServiceUrl("My Apps API terms of service")
        .contact(new Contact(
            "My Apps API Contact Email",
            "",
            ""))
        .license("My Apps API Licence Type")
        .licenseUrl("My Apps API License URL")
        .build();
  }
}
