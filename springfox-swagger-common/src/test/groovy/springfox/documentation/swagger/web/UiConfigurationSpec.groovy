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
package springfox.documentation.swagger.web

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import spock.lang.Specification

class UiConfigurationSpec extends Specification {
  def uiConfig = UiConfigurationBuilder.builder()
      .validatorUrl("validator:urn")
      .supportedSubmitMethods(UiConfiguration.Constants.NO_SUBMIT_METHODS)
      .docExpansion(DocExpansion.NONE)
      .operationsSorter(OperationsSorter.ALPHA)
      .defaultModelRendering(ModelRendering.MODEL)
      .build()
  def expected = "{" +
      "\"deepLinking\":true," +
      "\"displayOperationId\":false," +
      "\"defaultModelsExpandDepth\":1," +
      "\"defaultModelExpandDepth\":1," +
      "\"defaultModelRendering\":\"model\"," +
      "\"displayRequestDuration\":false," +
      "\"docExpansion\":\"none\"," +
      "\"filter\":false," +
      "\"operationsSorter\":" +
      "\"alpha\"," +
      "\"showExtensions\":false," +
      "\"showCommonExtensions\":false," +
      "\"tagsSorter\":\"alpha\"," +
      "\"validatorUrl\":\"validator:urn\"," +
      "\"supportedSubmitMethods\":[]" +
      "}"

  def uiConfigWithoutValidatorUrl  = UiConfigurationBuilder.builder()
      .supportedSubmitMethods(UiConfiguration.Constants.NO_SUBMIT_METHODS)
      .docExpansion(DocExpansion.NONE)
      .operationsSorter(OperationsSorter.ALPHA)
      .defaultModelRendering(ModelRendering.MODEL)
      .build()
  def expectedWithoutValidatorUrl = "{" +
      "\"deepLinking\":true," +
      "\"displayOperationId\":false," +
      "\"defaultModelsExpandDepth\":1," +
      "\"defaultModelExpandDepth\":1," +
      "\"defaultModelRendering\":\"model\"," +
      "\"displayRequestDuration\":false," +
      "\"docExpansion\":\"none\"," +
      "\"filter\":false," +
      "\"operationsSorter\":" +
      "\"alpha\"," +
      "\"showExtensions\":false," +
      "\"showCommonExtensions\":false," +
      "\"tagsSorter\":\"alpha\"," +
      "\"validatorUrl\":\"\"," +
      "\"supportedSubmitMethods\":[]" +
      "}"

  def "Renders non-null values using default ObjectMapper"() {
    given:
    ObjectMapper mapper = new ObjectMapper()

    when:
    def actual = mapper.writer().writeValueAsString(uiConfig)
    def actualWithoutValidatorUrl = mapper.writer().writeValueAsString(uiConfigWithoutValidatorUrl)

    then:
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(expectedWithoutValidatorUrl, actualWithoutValidatorUrl, JSONCompareMode.NON_EXTENSIBLE)
  }

  def "Renders non-null values using configured ObjectMapper"() {
    given:
    ObjectMapper mapper = new ObjectMapper()
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

    when:
    def actual = mapper.writer().writeValueAsString(uiConfig)
    def actualWithoutValidatorUrl = mapper.writer().writeValueAsString(uiConfigWithoutValidatorUrl)

    then:
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE)
    JSONAssert.assertEquals(expectedWithoutValidatorUrl, actualWithoutValidatorUrl, JSONCompareMode.NON_EXTENSIBLE)
  }
}
