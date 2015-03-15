package springdox.documentation.swagger.mappers

import com.fasterxml.classmate.TypeResolver
import spock.lang.Specification
import springdox.documentation.builders.ApiDescriptionBuilder
import springdox.documentation.builders.ApiInfoBuilder
import springdox.documentation.builders.ApiListingBuilder
import springdox.documentation.builders.AuthorizationBuilder
import springdox.documentation.builders.AuthorizationScopeBuilder
import springdox.documentation.builders.ModelBuilder
import springdox.documentation.builders.ModelPropertyBuilder
import springdox.documentation.builders.OperationBuilder
import springdox.documentation.builders.ParameterBuilder
import springdox.documentation.builders.ResourceListingBuilder
import springdox.documentation.builders.ResponseMessageBuilder
import springdox.documentation.schema.ModelRef
import springdox.documentation.service.AllowableListValues
import springdox.documentation.service.ApiListingReference
import springdox.documentation.spi.service.contexts.Defaults
import springdox.documentation.swagger.mixins.MapperSupport

import static com.google.common.collect.Sets.*

@Mixin(MapperSupport)
class ServiceModelToSwaggerMapperSpec extends Specification {
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
      def operation1 = new OperationBuilder()
                        .authorizations([new AuthorizationBuilder()
                          .type("basic")
                          .scopes(scope)
                          .build()])
                        .consumes(newHashSet("application/json"))
                        .produces(newHashSet("application/json"))
                        .deprecated("deprecated")
                        .method("operation1")
                        .nickname("op1")
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
                        .protocols(newHashSet("https"))
                        .responseClass("string")
                        .responseModel(new ModelRef("string"))
                        .responseMessages(newHashSet(response))
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
                  .authorizations(null)
                  .basePath("/base-path")
                  .description("listing")
                  .consumes([] as Set)
                  .produces([] as Set)
                  .models([
                    "m1" : new ModelBuilder()
                            .description("test")
                            .id("test")
                            .name("test")
                            .qualifiedType("qualified.name")
                            .subTypes(null)
                            .properties([
                              "p1" : new ModelPropertyBuilder()
                                      .allowableValues(null)
                                      .description("property 1")
                                      .modelRef(new ModelRef("String"))
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
      mappedOperation.nickname == builtOperation.nickname
      mappedOperation.authorizations.size() == builtOperation.authorizations.size()
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
                  .authorizations(null)
                  .info(new ApiInfoBuilder()
                    .contact("test@ssmvc.com")
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
      mapped.authorizations == built.authorizations
      mapped.swaggerVersion == "1.2"
      mapped.info.contact == built.info.contact
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
              .authorizations([])
              .info(new ApiInfoBuilder()
              .contact("test@ssmvc.com")
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
      mapped.authorizations == built.authorizations
      mapped.swaggerVersion == "1.2"
      mapped.info.contact == built.info.contact
      mapped.info.description == built.info.description
      mapped.info.license == built.info.license
      mapped.info.licenseUrl == built.info.licenseUrl
      mapped.info.termsOfServiceUrl == built.info.termsOfServiceUrl
      mapped.info.title == built.info.title
  }
}
