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

import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.*;

public class RenderOptions {

  private Optional<String> fileName;
  private String outputDir;
  private String examplesPath;
  private DocumentationFormat format;

  public RenderOptions(String outputDir, String examplesPath, DocumentationFormat format, String fileName) {
    this.fileName = Optional.fromNullable(emptyToNull(fileName));
    this.outputDir = outputDir;
    this.examplesPath = examplesPath;
    this.format = format;
  }

  public DocumentationFormat format() {
    return format;
  }

  public String examplesPath() {
    return examplesPath;
  }

  public String outputDir() {
    return outputDir;
  }

  public Optional<String> fileName() {
    return fileName;
  }

  public static RenderOptionsBuilder builder() {
    return new RenderOptionsBuilder();
  }

  public static class RenderOptionsBuilder {
    private String fileName;
    private String outputDir;
    private String examplesFolderPath;
    private DocumentationFormat format = DocumentationFormat.ASCIIDOC;

    /**
     * Builds RenderOptions which converts the Swagger response into Markup and writes into the
     * given {@code outputDir}.
     *
     * @return a Mock MVC {@code ResultHandler} that will produce the documentation
     * @see org.springframework.test.web.servlet.MockMvc#perform(org.springframework.test.web.servlet
     * .RequestBuilder)
     * @see org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
     * @throws NullPointerException if output directory is not specified
     */
    public RenderOptions build() {
      checkNotNull(emptyToNull(outputDir), "Output directory must be specified");
      return new RenderOptions(outputDir, nullToEmpty(examplesFolderPath), format, fileName);
    }

    /**
     * Specifies the directory to render the output to
     *
     * @param outputDir the directory to render the output to
     * @return the RenderOptionsBuilder
     */
    public RenderOptionsBuilder outputDir(String outputDir) {
      this.outputDir = outputDir;
      return this;
    }

    /**
     * Specifies the format which should be used to generate the files
     *
     * @param format the format which is used to generate the files
     * @return the RenderOptionsBuilder
     */
    public RenderOptionsBuilder format(DocumentationFormat format) {
      this.format = format;
      return this;
    }

    /**
     * Specifies the the name of the file generated
     *
     * @param fileName the name of the file to output, in the case of formats that generate a single file
     * @return the RenderOptionsBuilder
     */
    public RenderOptionsBuilder fileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    /**
     * Include examples into the Paths document
     *
     * @param examplesFolderPath the path to the folder where the example documents reside
     * @return the RenderOptionsBuilder
     */
    public RenderOptionsBuilder withExamples(String examplesFolderPath) {
      this.examplesFolderPath = examplesFolderPath;
      return this;
    }
  }
}
