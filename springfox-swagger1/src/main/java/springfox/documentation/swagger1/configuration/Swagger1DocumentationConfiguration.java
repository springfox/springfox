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

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.PathProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;
import springfox.documentation.swagger1.mappers.ServiceModelToSwaggerMapper;
import springfox.documentation.swagger1.web.Swagger1Controller;
import springfox.documentation.swagger1.web.SwaggerDefaultConfiguration;

@Configuration
@Import({SpringfoxWebConfiguration.class, SpringfoxWebMvcConfiguration.class, SwaggerCommonConfiguration.class})
@ComponentScan(basePackages = {
    "springfox.documentation.swagger1.readers.parameter",
    "springfox.documentation.swagger1.mappers"
})
public class Swagger1DocumentationConfiguration {

  @Bean
  public JacksonModuleRegistrar swagger1Module() {
    return new SwaggerJacksonModule();
  }

  @Bean
  public Swagger1Controller swagger1ControllerMapping(
      DocumentationCache documentationCache,
      ServiceModelToSwaggerMapper mapper,
      JsonSerializer jsonSerializer) {
    return new Swagger1Controller(documentationCache, mapper, jsonSerializer);
  }

  @Bean
  public SwaggerDefaultConfiguration swaggerDefaults(
      PathProvider pathProvider,
      TypeResolver type,
      Defaults defaults) {
    return new SwaggerDefaultConfiguration(defaults, type, pathProvider);
  }
}
