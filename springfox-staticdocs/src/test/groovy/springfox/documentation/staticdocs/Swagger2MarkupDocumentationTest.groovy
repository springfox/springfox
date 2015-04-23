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

import io.github.robwin.markup.builder.MarkupLanguage
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.DefaultMvcResult
import spock.lang.Specification

class Swagger2MarkupDocumentationTest extends Specification {

  def "should pass coverage"() {
    given:
      Swagger2MarkupResultHandler resultHandler = Swagger2MarkupResultHandler.outputDirectory("swagger_adoc")
              .withMarkupLanguage(MarkupLanguage.ASCIIDOC).build()
    when:
      resultHandler.handle(new DefaultMvcResult(new MockHttpServletRequest(), new MockHttpServletResponse()))
    then:
      thrown(IllegalArgumentException)
  }
}
