package springfox.documentation.spring.web.readers

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.builders.CompoundModelSpecificationBuilder
import springfox.documentation.builders.ModelSpecificationBuilder
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

import java.util.function.Consumer

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
   *             properties: {..}*           historyMetadata:
   *           # need to declare XML format!
   *           description: metadata in XML format
   *             type: object
   *             properties: {...}*           profileImage:
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
        .query { q
          ->
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
        .content { c
          ->
          c.representation(MediaType.MULTIPART_FORM_DATA)
              .apply(
                  { r
                    ->
                    r.model { m
                      ->
                      m.referenceModel(
                          new ReferenceModelSpecification(
                              new ModelKey(
                                  new QualifiedModelName(
                                      "io.springfox",
                                      "Address"),
                                  null,
                                  new ArrayList<>(),
                                  false)))
                    }
                  } as Consumer)
        }
        .build()
  }

  private RequestParameter historyMetadataParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("historyMetadata")
        .content { c
          ->
          c.representation(MediaType.MULTIPART_FORM_DATA)
              .apply(
                  { r
                    ->
                    r.model { m
                      ->
                      m.compoundModel { cm
                        ->
                        historyMetadataBuilder(cm)
                      }
                    }
                  } as Consumer)
        }
        .build()
  }

  private CompoundModelSpecificationBuilder historyMetadataBuilder(CompoundModelSpecificationBuilder cm, hidden = null) {
    cm.modelKey(new ModelKeyBuilder().build())
        .property("id")
        .apply(
            { p
              ->
              p.isHidden(hidden)
                  .type(
                  new ModelSpecificationBuilder()
                      .name("String")
                      .scalarModel(ScalarType.STRING)
                      .build())
                  .xml(new Xml().name("id").namespace("urn:io:springfox").prefix("sf"))
            } as Consumer)
        .property("version")
        .apply(
            { p
              ->
              p.isHidden(hidden)
                  .type(
                  new ModelSpecificationBuilder()
                      .name("String")
                      .scalarModel(ScalarType.BIGDECIMAL)
                      .build())
                  .xml(new Xml().name("version").namespace("urn:io:springfox").prefix("sf"))
            } as Consumer)
        .maxProperties(2)
        .minProperties(2)
  }

  private RequestParameter profileImageParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("profileImage")
        .content { c
          ->
          c.representation(MediaType.MULTIPART_FORM_DATA)
              .apply(
                  { r
                    ->
                    r.model {
                      m
                        ->
                        m.scalarModel(ScalarType.BINARY)
                    }
                        .encoding("profileImage")
                        .apply({
                          it.contentType("image/png, image/jpeg")
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
                        } as Consumer)
                  } as Consumer)
        }
        .build()
  }

  def expectedModel() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("body")
        .content {
          c
            ->
            c.representation(MediaType.MULTIPART_FORM_DATA)
                .apply({
                  r
                    ->
                    r.model {

                      m
                        ->
                        m.compoundModel { cm
                          ->
                          cm.modelKey(new ModelKeyBuilder()
                              .qualifiedModelName(new QualifiedModelName(
                                  "io.springfox",
                                  "profileImageAggregate"))
                              .build())
                              .property("id")
                              .apply({
                                p
                                  ->
                                  p.type(
                                      new ModelSpecificationBuilder()
                                          .scalarModel(ScalarType.UUID)
                                          .build())
                                      .required(false)
                              } as Consumer)
                              .property("address")
                              .apply({
                                p
                                  ->
                                  p.required(false)
                                      .type(
                                          new ModelSpecificationBuilder()
                                              .referenceModel(
                                                  new ReferenceModelSpecification(
                                                      new ModelKey(
                                                          new QualifiedModelName(
                                                              "io.springfox",
                                                              "Address"),
                                                          null,
                                                          new ArrayList<>(),
                                                          false)))
                                              .build())
                              } as Consumer)
                              .property("historyMetadata")
                              .apply({
                                cmp ->
                                  cmp.required(false)
                                      .type(new ModelSpecificationBuilder()
                                          .compoundModel {
                                            cmpc
                                              ->
                                              historyMetadataBuilder(cmpc, false)
                                          }
                                          .build())
                              } as Consumer)
                              .property("profileImage")
                              .apply({
                                p ->
                                  p.type(
                                      new ModelSpecificationBuilder()
                                          .scalarModel(ScalarType.BINARY)
                                          .build())
                                      .required(false)
                              } as Consumer)
                        }
                    }
                        .encoding("profileImage")
                        .apply({
                          it.contentType("image/png, image/jpeg")
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
                        } as Consumer)
                        .encoding("id")
                        .apply({
                          it.contentType("text/plain")
                        } as Consumer)

                } as Consumer)
        }
        .build()
  }
}
