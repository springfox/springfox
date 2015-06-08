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

import com.google.common.annotations.VisibleForTesting;
import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;

import java.io.IOException;

public class Swagger2MarkupRenderer implements DocumentationRenderer {
  @Override
  public void render(RenderOptions options, String json) throws IOException {
    Swagger2MarkupConverter.fromString(json)
        .withMarkupLanguage(convert(options.format()))
        .withExamples(options.examplesPath())
        .build()
        .intoFolder(options.outputDir());
  }

  @VisibleForTesting
  MarkupLanguage convert(DocumentationFormat format) {
    switch (format) {
      case ASCIIDOC:
        return MarkupLanguage.ASCIIDOC;
      case MARKDOWN:
        return MarkupLanguage.MARKDOWN;
      default:
        throw new UnsupportedOperationException("Unsupported format");
    }
  }
}
