/*
 *
 *  Copyright 2015-2018 the original author or authors.
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

package springfox.springconfig;

import com.fasterxml.classmate.TypeResolver;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import springfox.petstore.controller.PetController;

import java.util.List;

import static java.util.Collections.*;
import static springfox.documentation.schema.AlternateTypeRules.*;


@SpringBootApplication
@EnableSwagger2WebMvc//<1>
@ComponentScan(basePackageClasses = {
    PetController.class
})//<2>
public class Swagger2SpringBoot {

  public static void main(String[] args) {
    SpringApplication.run(Swagger2SpringBoot.class, args);
  }


  @Bean
  public Docket petApi() {
    return new Docket(DocumentationType.SWAGGER_2)//<3>
        .select() //<4>
        .apis(RequestHandlerSelectors.any()) //<5>
        .paths(PathSelectors.any()) //<6>
        .build() //<7>
        .pathMapping("/") //<8>
        .directModelSubstitute(LocalDate.class, String.class) //<9>
        .genericModelSubstitutes(ResponseEntity.class)
        .alternateTypeRules(
            newRule(typeResolver.resolve(DeferredResult.class,
                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                typeResolver.resolve(WildcardType.class))) //<10>
        .useDefaultResponseMessages(false) //<11>
        .globalResponseMessage(RequestMethod.GET, //<12>
            singletonList(new ResponseMessageBuilder()
                .code(500)
                .message("500 message")
                .responseModel(new ModelRef("Error")) //<13>
                .build()))
        .securitySchemes(singletonList(apiKey())) //<14>
        .securityContexts(singletonList(securityContext())) //<15>
        .enableUrlTemplating(true) //<21>
        .globalOperationParameters(//<22>
            singletonList(new ParameterBuilder()
                .name("someGlobalParameter")
                .description("Description of someGlobalParameter")
                .modelRef(new ModelRef("string"))
                .parameterType("query")
                .required(true)
                .build()))
        .tags(new Tag("Pet Service", "All apis relating to pets")) // <23>
        .additionalModels(typeResolver.resolve(AdditionalModel.class)); //<24>
  }

  @Autowired
  private TypeResolver typeResolver;

  private ApiKey apiKey() {
    return new ApiKey("mykey", "api_key", "header"); //<16>
  }

  private SecurityContext securityContext() {
    return SecurityContext.builder()
        .securityReferences(defaultAuth())
        .forPaths(PathSelectors.regex("/anyPath.*")) //<17>
        .build();
  }

  List<SecurityReference> defaultAuth() {
    AuthorizationScope authorizationScope
        = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    return singletonList(
        new SecurityReference("mykey", authorizationScopes)); //<18>
  }

  @Bean
  SecurityConfiguration security() {
    return SecurityConfigurationBuilder.builder() //<19>
        .clientId("test-app-client-id")
        .clientSecret("test-app-client-secret")
        .realm("test-app-realm")
        .appName("test-app")
        .scopeSeparator(",")
        .additionalQueryStringParams(null)
        .useBasicAuthenticationWithAccessCodeGrant(false)
        .enableCsrfSupport(false)
        .build();
  }

  @Bean
  UiConfiguration uiConfig() {
    return UiConfigurationBuilder.builder() //<20>
        .deepLinking(true)
        .displayOperationId(false)
        .defaultModelsExpandDepth(1)
        .defaultModelExpandDepth(1)
        .defaultModelRendering(ModelRendering.EXAMPLE)
        .displayRequestDuration(false)
        .docExpansion(DocExpansion.NONE)
        .filter(false)
        .maxDisplayedTags(null)
        .operationsSorter(OperationsSorter.ALPHA)
        .showExtensions(false)
        .showCommonExtensions(false)
        .tagsSorter(TagsSorter.ALPHA)
        .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
        .validatorUrl(null)
        .build();
  }

}
