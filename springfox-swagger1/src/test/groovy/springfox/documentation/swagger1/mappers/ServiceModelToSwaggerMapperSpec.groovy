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

package springfox.documentation.swagger1.mappers

import com.fasterxml.classmate.TypeResolver
import org.springframework.http.HttpMethod
import spock.lang.Specification
import springfox.documentation.builders.ApiDescriptionBuilder
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.ApiListingBuilder
import springfox.documentation.builders.AuthorizationScopeBuilder
import springfox.documentation.builders.ModelBuilder
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.ResourceListingBuilder
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.ModelRef
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.Contact
import springfox.documentation.service.SecurityReference
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator
import springfox.documentation.swagger1.mixins.MapperSupport

import static java.util.Collections.*

class ServiceModelToSwaggerMapperSpec extends Specification implements MapperSupport {
  def "Maps the api description correctly"() {
    given:
      def defaults = new Defaults()
      def scope = new AuthorizationScopeBuilder()
              .scope("test")
              .description("test scope")
              .build()
      def response = new ResponseMessageBuilder()
              .code(200)
              .message("Success")
              .responseModel(new ModelRef("string"))
              .build()
      def operation1 = new OperationBuilder(new CachingOperationNameGenerator())
                        .authorizations([SecurityReference.builder()
                          .reference("basic")
                          .scopes(scope)
                          .build()])
                        .consumes(singleton("application/json"))
                        .produces(singleton("application/json"))
                        .deprecated("deprecated")
                        .method(HttpMethod.POST)
                        .uniqueId("op1")
                        .notes("operation 1 notes")
                        .parameters([new ParameterBuilder()
                          .allowableValues(new AllowableListValues(["FIRST", "SECOND"], "string"))
                          .allowMultiple(false)
                          .defaultValue("FIRST")
                          .description("Chose first or second")
                          .name("order")
                          .parameterAccess("access")
                          .parameterType("body")
                          .modelRef(new ModelRef("string"))
                          .required(true)
                          .build()])
                        .position(1)
                        .codegenMethodNameStem("")
                        .protocols(singleton("https"))
                        .responseModel(new ModelRef("string"))
                        .responseMessages(singleton(response))
                      .build()
      def description = new ApiDescriptionBuilder(defaults.operationOrdering())
        .description("test")
        .hidden(true)
        .path("/api-path")
        .operations([operation1])
        .build()
      def built = new ApiListingBuilder(new Defaults().apiDescriptionOrdering())
                  .apis([description])
                  .apiVersion("1.0")
                  .securityReferences(null)
                  .basePath("/base-path")
                  .description("listing")
                  .consumes([] as Set)
                  .produces([] as Set)
                  .models([
                    "m1" : new ModelBuilder("test")
                            .description("test")
                            .name("test")
                            .qualifiedType("qualified.name")
                            .subTypes(null)
                            .properties([
                              "p1" : new ModelPropertyBuilder()
                                      .allowableValues(null)
                                      .description("property 1")
                                      .position(1)
                                      .qualifiedType("qualified.Test")
                                      .required(true)
                                      .type(new TypeResolver().resolve(String))
                                      .build()
                            ])
                            .build()])
                  .position(1)
                  .resourcePath("/resource-path")
                  .protocols(null)
                  .build()
      def sut = serviceMapper()
    when:
      def mappedListing = sut.toSwaggerApiListing(built)
    and:
      def mappedDescription = mappedListing.apis.first()
      def builtDescription = built.apis.first()
      def mappedOperation = mappedDescription.operations.first()
      def builtOperation = built.apis.first().operations.first()
    then:
      mappedDescription.description == built.apis.first().description
      mappedDescription.hidden == builtDescription.hidden
      mappedDescription.path == builtDescription.path
      mappedDescription.operations.size() == builtDescription.operations.size()
      mappedOperation.nickname == builtOperation.uniqueId
      mappedOperation.authorizations.size() == builtOperation.securityReferences.size()
      mappedOperation.authorizations.containsKey("basic")
      mappedOperation.consumes.first() == builtOperation.consumes.first()
      mappedOperation.produces.first() == builtOperation.produces.first()
      mappedOperation.parameters.size() == builtOperation.parameters.size()
      mappedOperation.parameters.first().name == builtOperation.parameters.first().name
      mappedOperation.position == builtOperation.position
      mappedOperation.notes == builtOperation.notes
      mappedOperation.dataType.absoluteType == "string"
      mappedOperation.deprecated == builtOperation.deprecated
      mappedOperation.protocol.first() == builtOperation.protocol.first()
      mappedOperation.responseMessages.size() == builtOperation.responseMessages.size()
      mappedOperation.responseMessages.first().code == builtOperation.responseMessages.first().code
      mappedOperation.responseMessages.first().message == builtOperation.responseMessages.first().message
      mappedOperation.responseMessages.first().responseModel == 
              builtOperation.responseMessages.first().responseModel.type
  }

  def "Resource listings are mapped correctly"() {
    given:
      def built = new ResourceListingBuilder()
                  .apis([new ApiListingReference("test", "test description", 1)])
                  .apiVersion("1.0")
                  .securitySchemes(null)
                  .info(new ApiInfoBuilder()
                    .contact(new Contact("test@ssmvc.com", "urn:test", "test@ssmvc.com"))
                    .description("test api")
                    .license("MIT")
                    .licenseUrl("urn:mit:lic")
                    .termsOfServiceUrl("urn:tos")
                    .title("Api Tester")
                    .build())
                  .build();
    when:
      def sut = serviceMapper()
      def mapped = sut.toSwaggerResourceListing(built)
    then:
      mapped.apis.size() == built.apis.size()
      mapped.apis.first().path == built.apis.first().path
      mapped.apis.first().description == built.apis.first().description
      mapped.apis.first().position == built.apis.first().position
      mapped.apiVersion == built.apiVersion
      mapped.authorizations == built.securitySchemes
      mapped.swaggerVersion == "1.2"
      mapped.info.contact == built.info.contact.name
      mapped.info.description == built.info.description
      mapped.info.license == built.info.license
      mapped.info.licenseUrl == built.info.licenseUrl
      mapped.info.termsOfServiceUrl == built.info.termsOfServiceUrl
      mapped.info.title == built.info.title
  }

  def "Resource listings are mapped correctly with authorizations"() {
    given:
      def built = new ResourceListingBuilder()
              .apis([new ApiListingReference("test", "test description", 1)])
              .apiVersion("1.0")
              .securitySchemes([])
              .info(new ApiInfoBuilder()
              .contact(new Contact("test@ssmvc.com", "urn:test", "test@ssmvc.com"))
              .description("test api")
              .license("MIT")
              .licenseUrl("urn:mit:lic")
              .termsOfServiceUrl("urn:tos")
              .title("Api Tester")
              .build())
              .build();
    when:
      def sut = serviceMapper()
      def mapped = sut.toSwaggerResourceListing(built)
    then:
      mapped.apis.size() == built.apis.size()
      mapped.apis.first().path == built.apis.first().path
      mapped.apis.first().description == built.apis.first().description
      mapped.apis.first().position == built.apis.first().position
      mapped.apiVersion == built.apiVersion
      mapped.authorizations == built.securitySchemes
      mapped.swaggerVersion == "1.2"
      mapped.info.contact == built.info.contact.name
      mapped.info.description == built.info.description
      mapped.info.license == built.info.license
      mapped.info.licenseUrl == built.info.licenseUrl
      mapped.info.termsOfServiceUrl == built.info.termsOfServiceUrl
      mapped.info.title == built.info.title
  }
}
