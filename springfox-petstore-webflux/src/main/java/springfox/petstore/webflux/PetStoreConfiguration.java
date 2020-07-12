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
package springfox.petstore.webflux;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.PathSelectors.*;

@Configuration
public class PetStoreConfiguration {

  private final TypeResolver resolver;

  public PetStoreConfiguration(TypeResolver resolver) {
    this.resolver = resolver;
  }

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
            .and(regex("/api/store/search.*").negate()))
            )
        .build()
        .host("petstore.swagger.io")
        .protocols(Stream.of("http", "https").collect(toSet()))
            .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
                    Arrays.asList(
                            AlternateTypeRules.newRule(
                                    resolver.resolve(Mono.class, WildcardType.class),
                                    resolver.resolve(WildcardType.class)),
                            AlternateTypeRules.newRule(
                                    resolver.resolve(ResponseEntity.class, WildcardType.class),
                                    resolver.resolve(WildcardType.class))
                    )))
            .alternateTypeRules(new RecursiveAlternateTypeRule(resolver,
                    Arrays.asList(
                            AlternateTypeRules.newRule(
                                    resolver.resolve(Flux.class, WildcardType.class),
                                    resolver.resolve(List.class, WildcardType.class)),
                            AlternateTypeRules.newRule(
                                    resolver.resolve(ResponseEntity.class, WildcardType.class),
                                    resolver.resolve(WildcardType.class))
                    )));
  }
}
