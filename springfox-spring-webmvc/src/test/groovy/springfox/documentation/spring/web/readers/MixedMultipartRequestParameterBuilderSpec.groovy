package springfox.documentation.spring.web.readers

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.builders.CompoundModelSpecificationBuilder
import springfox.documentation.builders.ModelSpecificationBuilder
import springfox.documentation.builders.RequestParameterBuilder
import springfox.documentation.schema.QualifiedModelName
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
   *             properties:
   *           historyMetadata:
   *             description: metadata in XML format
   *             type: object
   *             properties:
   *           profileImage:
   *             # default is application/octet-stream, need to declare an image type only!
   *             type: string
   *             format: binary
   *       encoding:
   *         id:
   *          contentType: text/plain
   *         address
   *          contentType: application/json
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

  RequestParameter idParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("id")
        .query { q
          ->
          q.model {
            it.scalarModel(ScalarType.UUID)
          }
        }
        .build()
  }

  RequestParameter addressParameter() {
    new RequestParameterBuilder()
        .accepts([MediaType.MULTIPART_FORM_DATA])
        .in(ParameterType.FORMDATA)
        .name("address")
        .content { c
          ->
          c.representation(MediaType.MULTIPART_FORM_DATA)
              .apply({
                r
                  ->
                  r.model { m
                    ->
                    m.referenceModel {
                      ref ->
                        ref.key {
                          key ->
                            key.qualifiedModelName {
                              q ->
                                q.name("Address")
                                    .namespace("io.springfox")
                            }
                                .isResponse(false)
                        }
                    }
                  }
                      .encoding("address")
                      .apply({ enc ->
                        enc.contentType("application/json")
                      } as Consumer)
              } as Consumer)
        }
        .build()
  }

  RequestParameter historyMetadataParameter() {
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
                        .encoding("historyMetadata")
                        .apply({
                          it.contentType("application/xml")
                        } as Consumer)
                  } as Consumer)
        }
        .build()
  }

  CompoundModelSpecificationBuilder historyMetadataBuilder(CompoundModelSpecificationBuilder cm, hidden = null) {
    cm
        .modelKey {
          mk ->
            mk.qualifiedModelName {
              qn ->
                qn.name("HistoryMetadata")
                    .namespace("some:namespace")
            }
        }
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
                          cm.modelKey { mk ->
                            mk.qualifiedModelName(new QualifiedModelName(
                                "io.springfox",
                                "profileImageAggregate"))
                          }
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
                                              .referenceModel {
                                                ref ->
                                                  ref.key {
                                                    key ->
                                                      key.qualifiedModelName {
                                                        q ->
                                                          q.name("Address")
                                                              .namespace("io.springfox")
                                                      }
                                                          .isResponse(false)
                                                  }
                                              }
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
                        .encoding("historyMetadata")
                        .apply({
                          it.contentType("application/xml")
                        } as Consumer)
                        .encoding("address")
                        .apply({
                          it.contentType("application/json")
                        } as Consumer)

                } as Consumer)
        }
        .build()
  }
}
