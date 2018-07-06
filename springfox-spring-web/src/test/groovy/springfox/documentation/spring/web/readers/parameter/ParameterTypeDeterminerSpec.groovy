package springfox.documentation.spring.web.readers.parameter

import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.http.HttpMethod.*
import static org.springframework.http.MediaType.*
import static springfox.documentation.spring.web.readers.parameter.ParameterTypeDeterminer.determineScalarParameterType

class ParameterTypeDeterminerSpec extends Specification {
  def "Cannot instantiate this class"() {
    when:
    new ParameterTypeDeterminer()

    then:
    thrown(UnsupportedOperationException)
  }

  @Unroll
  def "Determines the right parameter type given consumes media type #consumes and http method #httpMethod"() {
    when:
    def actual = determineScalarParameterType(
        consumes as Set,
        httpMethod)

    then:
    actual == expectedParameterType

    where:
    httpMethod | consumes                      | expectedParameterType
    PUT        | [APPLICATION_FORM_URLENCODED] | "query"
    POST       | [APPLICATION_FORM_URLENCODED] | "form"
    GET        | [APPLICATION_FORM_URLENCODED] | "query"
    PUT        | [MULTIPART_FORM_DATA]         | "query"
    POST       | [MULTIPART_FORM_DATA]         | "formData"
    GET        | [MULTIPART_FORM_DATA]         | "query"
  }
}
