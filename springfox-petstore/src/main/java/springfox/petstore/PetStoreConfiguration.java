/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.petstore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.PathSelectors.*;

@Configuration
public class PetStoreConfiguration {

  @Bean
  Docket petstore(List<SecurityScheme> authorizationTypes) {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("petstore")
        .useDefaultResponseMessages(false)
        .securitySchemes(authorizationTypes)
        .produces(Stream.of("application/xml", "application/json").collect(toSet()))
        .select()
        .paths((
            regex("/api/.*")
            .and(regex("/api/store/search.*").negate())
            .or(regex("/generic/.*"))))
        .build()
        .host("petstore.swagger.io")
        .protocols(Stream.of("http", "https").collect(toSet()));
  }
}
