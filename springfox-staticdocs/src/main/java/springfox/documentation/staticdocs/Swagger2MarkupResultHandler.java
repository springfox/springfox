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

import io.github.robwin.markup.builder.MarkupLanguage;
import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;


public class Swagger2MarkupResultHandler implements ResultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Swagger2MarkupResultHandler.class);

    private final String outputDir;
    private final MarkupLanguage markupLanguage;
    private final String examplesFolderPath;
    private final String encoding = "UTF-8";

    Swagger2MarkupResultHandler(String outputDir, MarkupLanguage markupLanguage, String examplesFolderPath) {
        this.outputDir = outputDir;
        this.markupLanguage = markupLanguage;
        this.examplesFolderPath = examplesFolderPath;
    }

    /**
     * Creates a Swagger2MarkupResultHandler.Builder
     *
     * @param outputDir the target folder
     * @return a Swagger2MarkupResultHandler.Builder
     */
    public static Builder outputDirectory(String outputDir) {
        Validate.notEmpty(outputDir, "outputDir must not be empty!");
        return new Builder(outputDir);
    }

    /**
     * Apply the action on the given result.
     *
     * @param result the result of the executed request
     * @throws Exception if a failure occurs
     */
    @Override
    public void handle(MvcResult result) throws Exception {
        MockHttpServletResponse response = result.getResponse();
        response.setCharacterEncoding(encoding);
        String swaggerJson = response.getContentAsString();
        Swagger2MarkupConverter.fromString(swaggerJson).withMarkupLanguage(markupLanguage)
                .withExamples(examplesFolderPath).build().intoFolder(outputDir);
    }


    public static class Builder {
        private final String outputDir;
        private String examplesFolderPath;
        private MarkupLanguage markupLanguage = MarkupLanguage.ASCIIDOC;

        Builder(String outputDir) {
            this.outputDir = outputDir;
        }

        /**
         * Builds Swagger2MarkupResultHandler which converts the Swagger response into Markup and writes into the given {@code
         * outputDir}.
         *
         * @return a Mock MVC {@code ResultHandler} that will produce the documentation
         * @see org.springframework.test.web.servlet.MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
         * @see org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
         */
        public Swagger2MarkupResultHandler build() {
              return new Swagger2MarkupResultHandler(outputDir, markupLanguage,
                    examplesFolderPath);
        }

        /**
         * Specifies the markup language which should be used to generate the files
         *
         * @param markupLanguage the markup language which is used to generate the files
         * @return the Swagger2MarkupConverter.Builder
         */
        public Builder withMarkupLanguage(MarkupLanguage markupLanguage) {
            this.markupLanguage = markupLanguage;
            return this;
        }

        /**
         * Include examples into the Paths document
         *
         * @param examplesFolderPath the path to the folder where the example documents reside
         * @return the Swagger2MarkupConverter.Builder
         */
        public Builder withExamples(String examplesFolderPath) {
            this.examplesFolderPath = examplesFolderPath;
            return this;
        }
    }
}
