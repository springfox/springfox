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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.newBufferedWriter;

public class JsonRenderer implements DocumentationRenderer {
  @Override
  public void render(RenderOptions options, String json) throws IOException {
    Files.createDirectories(Paths.get(options.outputDir()));
    BufferedWriter writer = null;
    try {
      writer = newBufferedWriter(
          Paths.get(options.outputDir(), options.fileName().or("swagger.json")),
          StandardCharsets.UTF_8);
      writer.write(json);
      writer.flush();
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
