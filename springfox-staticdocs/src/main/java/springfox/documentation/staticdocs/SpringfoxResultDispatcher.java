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

package springfox.documentation.staticdocs;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import java.io.IOException;
import java.util.Map;


public class SpringfoxResultDispatcher implements ResultHandler {

  private static final Map<DocumentationFormat, DocumentationRenderer> renderers
      = ImmutableMap.<DocumentationFormat, DocumentationRenderer>builder()
      .put(DocumentationFormat.ASCIIDOC, new Swagger2MarkupRenderer())
      .put(DocumentationFormat.MARKDOWN, new Swagger2MarkupRenderer())
      .put(DocumentationFormat.JSON, new JsonRenderer())
      .build();
  private final RenderOptions renderOptions;

  SpringfoxResultDispatcher(RenderOptions renderOptions) {
    this.renderOptions = renderOptions;
  }

  /**
   * Apply the action on the given result.
   *
   * @param result the result of the executed request
   * @throws Exception if a failure occurs
   */
  @Override
  public void handle(MvcResult result) throws IOException {
    MockHttpServletResponse response = result.getResponse();
    String swaggerJson = response.getContentAsString();
    //noinspection ConstantConditions
    Functions.forMap(renderers)
        .apply(renderOptions.format())
        .render(renderOptions, swaggerJson);
  }


}
