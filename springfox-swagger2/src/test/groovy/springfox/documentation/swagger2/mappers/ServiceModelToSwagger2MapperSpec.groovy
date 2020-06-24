package springfox.documentation.swagger2.mappers

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.classmate.types.ResolvedObjectType
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.builders.ApiDescriptionBuilder
import springfox.documentation.builders.ApiListingBuilder
import springfox.documentation.builders.AuthorizationScopeBuilder
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.builders.ModelBuilder
import springfox.documentation.builders.ModelPropertyBuilder
import springfox.documentation.builders.ModelSpecificationBuilder
import springfox.documentation.builders.OperationBuilder
import springfox.documentation.builders.ParameterBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.builders.ResponseBuilder
import springfox.documentation.builders.ResponseMessageBuilder
import springfox.documentation.schema.Example
import springfox.documentation.schema.ModelRef
import springfox.documentation.schema.ModelReference
import springfox.documentation.schema.ScalarType
import springfox.documentation.service.AllowableListValues
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Documentation
import springfox.documentation.service.ModelNamesRegistry
import springfox.documentation.service.ObjectVendorExtension
import springfox.documentation.service.ParameterStyle
import springfox.documentation.service.ParameterType
import springfox.documentation.service.SecurityReference
import springfox.documentation.service.StringVendorExtension
import springfox.documentation.service.Tag
import springfox.documentation.spi.service.contexts.Defaults
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator

import java.util.function.Consumer
import java.util.function.Function

import static java.util.Collections.*

class ServiceModelToSwagger2MapperSpec extends Specification implements MapperSupport {
  def "Maps the api operation correctly"() {
    given:
    def built = apiListing()
    def sut = swagger2Mapper()

    when:
    def apiListings = new HashMap<>()
    apiListings.putIfAbsent(
        "new",
        new ArrayList<>())
    apiListings.get("new").add(built)
    def mappedListing = sut.mapApiListings(apiListings)

    and:
    def mappedPath = mappedListing.entrySet().first()
    def builtDescription = built.apis.first()
    def mappedOperation = mappedPath.value.post
    def builtOperation = built.apis.first().operations.first()

    then:
    mappedPath.key == builtDescription.path
    mappedOperation.operationId == builtOperation.uniqueId
    mappedOperation.security.size() == builtOperation.securityReferences.size()
    mappedOperation.security.first() containsKey("basic")
    mappedOperation.consumes.first() == builtOperation.consumes.first()
    mappedOperation.produces.first() == builtOperation.produces.first()
    mappedOperation.parameters.size() == builtOperation.parameters.size()
    mappedOperation.parameters.first().name == builtOperation.parameters.first().name
    mappedOperation.description == builtOperation.notes
    mappedOperation.deprecated == Boolean.parseBoolean(builtOperation.deprecated)
    mappedOperation.schemes.first().toValue() == builtOperation.protocol.first().toLowerCase()
    mappedOperation.responses.size() == builtOperation.responseMessages.size()
    mappedOperation.responses.get("200").description == builtOperation.responseMessages.first().message
    mappedOperation.responses.get("200").schema.type == "string"
    mappedOperation.responses.get("200").examples.get("mediaType") == "value"
    mappedOperation.vendorExtensions.size() == builtOperation.vendorExtensions.size()
    mappedOperation.vendorExtensions.containsKey("x-test1")
    mappedOperation.vendorExtensions.containsKey("x-test2")
  }

  def "Maps documentation to swagger models"() {
    given:
    Documentation documentation = new DocumentationBuilder()
        .basePath("base:uri")
        .consumes(["application/json"] as Set)
        .name("doc-group")
        .host("test")
        .schemes(["https"] as Set)
        .tags(
            [new Tag(
                "tag",
                "tag description",
                [new StringVendorExtension(
                    "x-test3",
                    "value")])] as Set)
        .build()

    when:
    def sut = swagger2Mapper()

    and:
    def mapped = sut.mapDocumentation(documentation)

    then:
    mapped.basePath == documentation.basePath
    mapped.consumes.containsAll(documentation.consumes)
    mapped.produces.isEmpty()
    mapped.schemes.first().toValue() == documentation.schemes.first()
    mapped.host == documentation.host
    mapped.definitions.isEmpty()
    mapped.tags.first().name == "tag"
    mapped.tags.first().description == "tag description"
    mapped.tags.first().vendorExtensions.containsKey("x-test3")
  }

  def "Maps documentation with resource listing to swagger models"() {
    given:
    Documentation documentation = new DocumentationBuilder()
        .basePath("base:uri")
        .produces(["application/json"] as Set)
        .name("doc-group")
        .resourceListing {
          it.apiVersion("1.0")
              .info(ApiInfo.DEFAULT)
        }
        .build()

    when:
    def sut = swagger2Mapper()

    and:
    def mapped = sut.mapDocumentation(documentation)

    then:
    mapped.basePath == documentation.basePath
    mapped.produces.containsAll(documentation.produces)
    mapped.consumes.isEmpty()
    mapped.definitions.isEmpty()
    mapped.tags.isEmpty()
    mapped.info != null
  }

  def "Maps documentation with api listing to swagger models"() {
    given:
    def listingLookup = new HashMap<>()
    listingLookup.putIfAbsent(
        "test",
        new LinkedList<>())
    listingLookup.get("test").add(apiListing())
    Documentation documentation = new DocumentationBuilder()
        .basePath("base:uri")
        .produces(["application/json"] as Set)
        .name("doc-group")
        .resourceListing {
          it.apiVersion("1.0")
        }
        .apiListingsByResourceGroupName(listingLookup)
        .build()

    when:
    def sut = swagger2Mapper()

    and:
    def mapped = sut.mapDocumentation(documentation)

    then:
    mapped.basePath == documentation.basePath
    mapped.paths.containsKey("/api-path")
    mapped.definitions.containsKey("m1")
    mapped.tags.isEmpty()
    mapped.info == null
  }

  def "Maps api info to swagger models"() {
    given:
    def sut = swagger2Mapper()
    def apiInfo = ApiInfo.DEFAULT

    when:
    def mapped = sut.mapApiInfo(apiInfo)

    then:
    mapped.contact.name == apiInfo.contact.name
    mapped.contact.email == apiInfo.contact.name
    mapped.contact.url == apiInfo.contact.url
    mapped.description == apiInfo.description
    mapped.license.name == apiInfo.license
    mapped.license.url == apiInfo.licenseUrl
    mapped.termsOfService == apiInfo.termsOfServiceUrl
    mapped.title == apiInfo.title
    mapped.version == apiInfo.version
  }

  def "Maps empty api info to swagger models"() {
    given:
    def sut = swagger2Mapper()
    def apiInfo = null

    when:
    def mapped = sut.mapApiInfo(apiInfo)

    then:
    mapped == null
  }

  def apiListing() {
    def defaults = new Defaults()
    def scope = new AuthorizationScopeBuilder()
        .scope("test")
        .description("test scope")
        .build()
    def example = new Example(
        "mediaType",
        "value")
    def response = new ResponseMessageBuilder()
        .code(200)
        .message("Success")
        .responseModel(new ModelRef("string"))
        .examples([example])
        .build()

    def first = new ObjectVendorExtension("")
    first.with {
      addProperty(
          new StringVendorExtension(
              "x-test1",
              "test1"))
    }

    def second = new ObjectVendorExtension("x-test2")
    second.with {
      addProperty(
          new StringVendorExtension(
              "name2",
              "test2"))
    }

    def operation1 = new OperationBuilder(new CachingOperationNameGenerator())
        .authorizations(
            [SecurityReference.builder()
                 .reference("basic")
                 .scopes(scope)
                 .build()])
        .consumes(singleton("application/json"))
        .produces(singleton("application/json"))
        .deprecated("true")
        .method(HttpMethod.POST)
        .uniqueId("op1")
        .notes("operation 1 notes")
        .tags(singleton("sometag"))
        .parameters(
            [new ParameterBuilder()
                 .allowableValues(
                     new AllowableListValues(
                         ["FIRST", "SECOND"],
                         "string"))
                 .allowMultiple(false)
                 .defaultValue("FIRST")
                 .description("Chose first or second")
                 .name("order")
                 .parameterAccess("access")
                 .parameterType("body")
                 .modelRef(new ModelRef("string"))
                 .required(true)
                 .build()])
        .requestParameters(
            [new RequestParameterBuilder()
                 .required(true)
                 .name("order")
                 .description("Chose first or second")
                 .in(ParameterType.FORM)
                 .query { q ->
                   q.style(ParameterStyle.FORM)
                       .enumerationFacet { e
                         ->
                         e.allowedValues(["FIRST", "SECOND"])
                       }
                       .model {
                         it.scalarModel(ScalarType.STRING)
                       }
                 }
                 .build()])
        .position(1)
        .codegenMethodNameStem("")
        .protocols(singleton("HTTPS"))
        .responseModel(new ModelRef("string"))
        .responseMessages(singleton(response))
        .responses(
            [new ResponseBuilder()
                 .code("200")
                 .description("Success")
                 .examples([example])
                 .representation(MediaType.TEXT_PLAIN)
                 .apply({
                   it.model { m -> m.scalarModel(ScalarType.STRING) }
                 } as Consumer)
                 .build()] as Set)
        .extensions([first, second])
        .build()
    def description = new ApiDescriptionBuilder(defaults.operationOrdering())
        .description("test")
        .hidden(true)
        .path("/api-path")
        .operations([operation1])
        .build()

    ResolvedType resolved = new TypeResolver().resolve(String)
    def modelProperty = new ModelPropertyBuilder()
        .allowableValues(null)
        .name("p1")
        .description("property 1")
        .position(1)
        .qualifiedType("qualified.Test")
        .required(true)
        .type(resolved)
        .build()
    modelProperty.updateModelRef(createFactory(new ModelRef("string")))
    def modelSpecs = ["m1":
                          new ModelSpecificationBuilder()
                              .scalarModel(ScalarType.STRING)
                              .name("test")
                              .build()]
    def modelNames = Mock(ModelNamesRegistry)
    modelNames.modelsByName() >> modelSpecs
    new ApiListingBuilder(defaults.apiDescriptionOrdering())
        .apis([description])
        .apiVersion("1.0")
        .securityReferences(null)
        .basePath("/base-path")
        .tags(
            [new Tag(
                "some-group",
                "")] as Set)
        .description("listing")
        .consumes([] as Set)
        .produces([] as Set)
        .models(
            [
                "m1": new ModelBuilder("test")
                    .description("test")
                    .name("test")
                    .type(new TypeResolver().resolve(String))
                    .qualifiedType("qualified.name")
                    .subTypes(null)
                    .properties(
                        [
                            "p1": modelProperty
                        ])
                    .build()])
        .modelSpecifications(modelSpecs)
        .modelNamesRegistry(modelNames)
        .position(1)
        .resourcePath("/resource-path")
        .protocols(null)
        .build()
  }

  Function createFactory(ModelRef modelRef) {
    new Function<ResolvedObjectType, ModelReference>() {
      @Override
      ModelReference apply(ResolvedObjectType type) {
        return modelRef
      }
    }
  }

}
