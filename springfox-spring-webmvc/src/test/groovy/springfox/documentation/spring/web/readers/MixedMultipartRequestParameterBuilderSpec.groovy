package springfox.documentation.spring.web.readers

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.builders.CompoundModelSpecificationBuilder
import springfox.documentation.builders.ModelSpecificationBuilder
import springfox.documentation.builders.RepresentationBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.ModelKey
import springfox.documentation.schema.ReferenceModelSpecification
import springfox.documentation.schema.ScalarType
import springfox.documentation.schema.Xml
import springfox.documentation.service.Header
import springfox.documentation.service.ParameterType
import springfox.documentation.service.RequestParameter
import springfox.documentation.spring.web.readers.operation.ContentParameterAggregator

class MixedMultipartRequestParameterBuilderSpec extends Specification {
  /**
   * requestBody:
   *   content:
   *     multipart/mixed:
   *       schema:
   *         type: object
   *         properties:
   *           id:
   *             # default is text/plain
   *             type: string
   *             format: uuid
   *           address:
   *             # default is application/json
   *             type: object
   *             properties: {}*           historyMetadata:
   *           # need to declare XML format!
   *           description: metadata in XML format
   *             type: object
   *             properties: {}*           profileImage:
   *             # default is application/octet-stream, need to declare an image type only!
   *             type: string
   *             format: binary
   *       encoding:
   *         historyMetadata:
   *           # require XML Content-Type in utf-8 encoding
   *           contentType: application/xml; charset=utf-8
   *         profileImage:
   *           # only accept png/jpeg
   *           contentType: image/png, image/jpeg
   *           headers:
   *             X-Rate-Limit-Limit:
   *               description: The number of allowed requests in the current period
   *               schema:
   *                 type: integer
   */
  def "RequestParameterBuilder is able to handle multipart mixed message"() {
    given:
    def parameters = [
        idParameter(),
        addressParameter(),
        historyMetadataParameter(),
        profileImageParameter()]

    when:
    def aggregated = new ContentParameterAggregator().aggregate(parameters)

    then:
    aggregated.size() == 1

    and:
    aggregated.first().parameterSpecification.content.isPresent()
    def content = aggregated.first().parameterSpecification.content.get()
    content.representations.size() == 1

    and:
    content.representations.first().model.compound.isPresent()
    def model = content.representations.first().model.compound.get()
    model.properties.size() == 4

    and:
    aggregated.first() == expectedModel()


  }

  private RequestParameter idParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("id")
        .simpleParameterBuilder()
        .model(
            new ModelSpecificationBuilder("1")
                .scalarModel(ScalarType.UUID)
                .build())
        .yield()
        .build()
  }

  private RequestParameter addressParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("address")
        .contentSpecificationBuilder()
        .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
        .modelSpecificationBuilder("abc")
        .referenceModel(
            new ReferenceModelSpecification(
                new ModelKey(
                    "io.springfox",
                    "Address",
                    false)))
        .yield(RequestParameterBuilder)
        .yield()
        .yield()
        .build()
  }

  private RequestParameter historyMetadataParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("historyMetadata")
        .contentSpecificationBuilder()
        .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
        .modelSpecificationBuilder("1")
        .compoundModelBuilder()
        .propertyBuilder("id")
        .type(
            new ModelSpecificationBuilder("2")
                .name("String")
                .scalarModel(ScalarType.STRING)
                .build())
        .xml(new Xml().name("id").namespace("urn:io:springfox").prefix("sf"))
        .yield()
        .propertyBuilder("version")
        .type(
            new ModelSpecificationBuilder("3")
                .name("String")
                .scalarModel(ScalarType.BIGDECIMAL)
                .build())
        .xml(new Xml().name("version").namespace("urn:io:springfox").prefix("sf"))
        .yield()
        .maxProperties(2)
        .minProperties(2)
        .yield()
        .yield(RequestParameterBuilder)
        .yield()
        .yield()
        .build()
  }

  private RequestParameter profileImageParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("profileImage")
        .contentSpecificationBuilder()
        .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
        .modelSpecificationBuilder("4")
        .scalarModel(ScalarType.BINARY)
        .yield(RepresentationBuilder)
        .encodingForProperty("profileImage")
        .contentType("image/png, image/jpeg")
        .headers(
            [new Header(
                " X-Rate-Limit-Limit",
                "The number of allowed requests in the current period",
                null,
                new ModelSpecificationBuilder("5")
                    .name("Integer")
                    .scalarModel(ScalarType.INTEGER)
                    .build())])
        .yield()
        .yield()
        .yield()
        .build()
  }

  def expectedModel() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("body")
        .contentSpecificationBuilder()
          .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
            .modelSpecificationBuilder("4")
              .compoundModelBuilder()
                .propertyBuilder("id")
                .type(new ModelSpecificationBuilder("")
                    .scalarModel(ScalarType.STRING)
                    .build())
                .yield()
                .propertyBuilder("address")
                  .type(new ModelSpecificationBuilder("")
                    .referenceModel(new ReferenceModelSpecification(new ModelKey("123", "abc", true)))
                  .build())
                .yield()
                .propertyBuilder("historyMetadata")
                .yield()
                .propertyBuilder("profileImage")
                .yield()
              .yield()
            .yield(RepresentationBuilder)
          .encodingForProperty("profileImage")
                .contentType("image/png, image/jpeg")
                .headers([new Header(
                  " X-Rate-Limit-Limit",
                  "The number of allowed requests in the current period",
                  null,
                  new ModelSpecificationBuilder("5")
                    .name("Integer")
                    .scalarModel(ScalarType.INTEGER)
                    .build())])
              .yield()
            .yield()
          .yield()
        .build()
  }
}
