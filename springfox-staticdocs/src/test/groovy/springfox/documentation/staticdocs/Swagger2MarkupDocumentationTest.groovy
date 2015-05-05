/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.staticdocs

import groovy.io.FileType
import io.github.robwin.markup.builder.MarkupLanguage
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.DefaultMvcResult
import spock.lang.Shared
import spock.lang.Specification

class Swagger2MarkupDocumentationTest extends Specification {

  @Shared
  MockHttpServletResponse response

  def setup() {
    response = new MockHttpServletResponse()
    PrintWriter writer = response.getWriter()
    writer.write(Swagger2MarkupDocumentationTest.class.getResource('/swagger.json').text)
    writer.flush()
  }

  def "should convert swagger json into three asciidoc files"() {
    given:
      SpringfoxResultDispatcher resultHandler = SpringfoxResultDispatcher.outputDirectory('build/docs/asciidoc')
              .withMarkupLanguage(MarkupLanguage.ASCIIDOC).build()
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response))
    then:
      def list = []
      def dir = new File(resultHandler.outputDir)
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
  }


  def "should create swagger json with custom file name"() {
    given:
      SwaggerResultHandler resultHandler = SwaggerResultHandler.outputDirectory('build/docs/swagger/custom')
              .withFileName("custom.json").build()
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response));
    then:
      def list = []
      def dir = new File(resultHandler.outputDir)
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list == ['custom.json']
  }

  def "should create swagger json file with default file name"() {
    given:
      SwaggerResultHandler resultHandler = SwaggerResultHandler.outputDirectory('build/docs/swagger/default').build()
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response));
    then:
      def list = []
      def dir = new File(resultHandler.outputDir)
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list == ['swagger.json']
  }
}
