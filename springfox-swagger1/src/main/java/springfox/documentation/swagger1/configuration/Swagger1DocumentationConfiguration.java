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

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.schema.configuration.ObjectMapperConfigured;
import springfox.documentation.spring.web.SpringMvcDocumentationConfiguration;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;

import static springfox.documentation.swagger1.configuration.SwaggerJacksonModule.*;

@Configuration
@Import({ SpringMvcDocumentationConfiguration.class, SwaggerCommonConfiguration.class})
@ComponentScan(basePackages = {
        "springfox.documentation.swagger1.readers.parameter",
        "springfox.documentation.swagger1.web",
        "springfox.documentation.swagger1.mappers"
})
public class Swagger1DocumentationConfiguration implements ApplicationListener<ObjectMapperConfigured> {
  @Override
  public void onApplicationEvent(ObjectMapperConfigured event) {
    maybeRegisterModule(event.getObjectMapper());
  }
}
