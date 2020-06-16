package springfox.documentation.spring.web.readers

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.builders.ModelSpecificationBuilder
import springfox.documentation.builders.RepresentationBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.ModelKey
import springfox.documentation.schema.ModelKeyBuilder
import springfox.documentation.schema.QualifiedModelName
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
        .query {q ->
          q.model(
              new ModelSpecificationBuilder()
                  .scalarModel(ScalarType.UUID)
                  .build())
        }
        .build()
  }

  private RequestParameter addressParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("address")
        .content {c
          ->
          c.representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
           .modelSpecificationBuilder()
           .referenceModel(
               new ReferenceModelSpecification(
                   new ModelKey(
                       new QualifiedModelName(
                           "io.springfox",
                           "Address"),
                       null,
                       new ArrayList<>(),
                       false)))
           .yield(RequestParameterBuilder)
           .yield()
        }
        .build()
  }

  private RequestParameter historyMetadataParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("historyMetadata")
        .content {c
          ->
          c.representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
           .modelSpecificationBuilder()
           .compoundModelBuilder()
           .modelKey(new ModelKeyBuilder().build())
           .propertyBuilder("id")
           .type(
               new ModelSpecificationBuilder()
                   .name("String")
                   .scalarModel(ScalarType.STRING)
                   .build())
           .xml(new Xml().name("id").namespace("urn:io:springfox").prefix("sf"))
           .yield()
           .propertyBuilder("version")
           .type(
               new ModelSpecificationBuilder()
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
        }
        .build()
  }

  private RequestParameter profileImageParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("profileImage")
        .content {c
          ->
          c.representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
           .modelSpecificationBuilder()
           .scalarModel(ScalarType.BINARY)
           .yield(RepresentationBuilder)
           .encodingForProperty("profileImage")
           .contentType("image/png, image/jpeg")
           .headers(
               [new Header(
                   " X-Rate-Limit-Limit",
                   "The number of allowed requests in the current period",
                   null,
                   new ModelSpecificationBuilder()
                       .name("Integer")
                       .scalarModel(ScalarType.INTEGER)
                       .build()
               )])
           .yield()
           .yield()
        }
        .build()
  }

  def expectedModel() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("body")
        .content {c
          ->
          c.representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
           .modelSpecificationBuilder()
           .compoundModelBuilder()
           .modelKey(new ModelKeyBuilder().build())
           .propertyBuilder("id")
           .type(
               new ModelSpecificationBuilder()
                   .scalarModel(ScalarType.STRING)
                   .build())
           .yield()
           .propertyBuilder("address")
           .type(
               new ModelSpecificationBuilder()
                   .referenceModel(
                       new ReferenceModelSpecification(
                           new ModelKey(
                               new QualifiedModelName(
                                   "123",
                                   "abc"),
                               null,
                               new ArrayList<>(),
                               true)))
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
           .headers(
               [new Header(
                   " X-Rate-Limit-Limit",
                   "The number of allowed requests in the current period",
                   null,
                   new ModelSpecificationBuilder()
                       .name("Integer")
                       .scalarModel(ScalarType.INTEGER)
                       .build()
               )])
           .yield()
           .yield()
        }
        .build()
  }
}
