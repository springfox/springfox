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
      RenderOptions renderOptions = RenderOptions.builder()
          .outputDir('build/docs/asciidoc')
          .format(DocumentationFormat.ASCIIDOC)
          .build()
      def resultHandler = new SpringfoxResultDispatcher(renderOptions)
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response))
    then:
      def list = []
      def dir = new File(renderOptions.outputDir())
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list.sort() == ['definitions.adoc', 'overview.adoc', 'paths.adoc']
  }


  def "should create swagger json with custom file name"() {
    given:
      RenderOptions renderOptions = RenderOptions.builder()
          .outputDir('build/docs/swagger/custom')
          .format(DocumentationFormat.JSON)
          .fileName("custom.json")
          .build()
      def resultHandler = new SpringfoxResultDispatcher(renderOptions)
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response));
    then:
      def list = []
      def dir = new File(renderOptions.outputDir())
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list == ['custom.json']
  }

  def "should create swagger json file with default file name"() {
    given:
      RenderOptions renderOptions = RenderOptions.builder()
          .outputDir('build/docs/swagger/default')
          .format(DocumentationFormat.JSON)
          .build()
      def resultHandler = new SpringfoxResultDispatcher(renderOptions)
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), response));
    then:
      def list = []
      def dir = new File(renderOptions.outputDir())
      dir.eachFileRecurse(FileType.FILES) { file ->
        list << file.name
      }
      list == ['swagger.json']
  }
}
