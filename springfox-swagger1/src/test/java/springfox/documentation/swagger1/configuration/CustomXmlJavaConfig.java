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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger1.annotations.EnableSwagger;

@Configuration
@EnableSwagger
@EnableWebMvc
public class CustomXmlJavaConfig {
  @Bean
  public Docket customImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("customPlugin")
        .select()
          .paths(PathSelectors.regex(".*pet.*"))
          .build();
  }

  @Bean
  public Docket secondCustomImplementation() {
    return new Docket(DocumentationType.SWAGGER_12)
        .groupName("secondCustomPlugin")
        .select()
          .paths(PathSelectors.regex("/feature.*"))
          .build();
  }
}
