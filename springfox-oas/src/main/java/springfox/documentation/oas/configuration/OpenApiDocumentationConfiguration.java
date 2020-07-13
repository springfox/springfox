/*
 *
 *  Copyright 2017-2018 the original author or authors.
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

package springfox.documentation.oas.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.web.SpringfoxWebConfiguration;
import springfox.documentation.spring.web.SpringfoxWebFluxConfiguration;
import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;
import springfox.documentation.swagger.configuration.SwaggerCommonConfiguration;

@Configuration
@Import({
    SpringfoxWebConfiguration.class,
    SpringfoxWebMvcConfiguration.class,
    SpringfoxWebFluxConfiguration.class,
    SwaggerCommonConfiguration.class,
    OpenApiMappingConfiguration.class,
    OpenApiWebMvcConfiguration.class,
    OpenApiWebFluxConfiguration.class
})
@ComponentScan(basePackages = {
    "springfox.documentation.oas.web",
    "springfox.documentation.oas.mappers"
})
public class OpenApiDocumentationConfiguration {
}
