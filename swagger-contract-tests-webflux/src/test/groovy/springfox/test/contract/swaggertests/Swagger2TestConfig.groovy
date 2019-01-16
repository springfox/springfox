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
package springfox.test.contract.swaggertests

import com.fasterxml.classmate.TypeResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import springfox.documentation.core.schema.AlternateTypeRules
import springfox.documentation.schema.RecursiveAlternateTypeRule
import springfox.documentation.core.schema.WildcardType
import springfox.documentation.core.service.SecurityScheme
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux
import springfox.petstore.webflux.PetStoreConfiguration

import static springfox.documentation.core.builders.PathSelectors.regex

@Configuration
@EnableSwagger2WebFlux
@Import([PetStoreConfiguration])
class Swagger2TestConfig {

    @Autowired
    private TypeResolver resolver

    @Bean
    Docket petstoreWithUriTemplating(List<SecurityScheme> authorizationTypes) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("petstoreTemplated")
                .useDefaultResponseMessages(false)
                .securitySchemes(authorizationTypes)
                .produces(['application/xml', 'application/json'] as Set)
                .select()
                .paths(regex("/api/store/search.*"))
                .build()
                .enableUrlTemplating(true)
                .host("petstore.swagger.io")
                .protocols(['http', 'https'] as Set)
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
                )))
    }

    @Bean
    Docket groovyServiceBean(List<SecurityScheme> authorizationTypes) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("groovyService")
                .useDefaultResponseMessages(false)
                .securitySchemes(authorizationTypes)
                .forCodeGeneration(true)
                .produces(['application/xml', 'application/json'] as Set)
                .select()
                .paths(regex("/groovy/.*"))
                .build()
                .ignoredParameterTypes(MetaClass)
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
                )))
    }
}
