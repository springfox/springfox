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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SwaggerResultHandler implements ResultHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SwaggerResultHandler.class);

    private final String outputDir;
    private final String fileName;

    SwaggerResultHandler(String outputDir, String fileName) {
        this.outputDir = outputDir;
        this.fileName = fileName;
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
        String swaggerJson = response.getContentAsString();
        Files.createDirectories(Paths.get(outputDir));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, fileName), StandardCharsets.UTF_8)){
            writer.write(swaggerJson);
        }
    }

    public static class Builder {
        private final String outputDir;
        private String fileName;

        Builder(String outputDir) {
            this.outputDir = outputDir;
        }

        /**
         * Builds SwaggerResultHandler which stores the Swagger response into file the {@code
         * outputDir} and {@code fileName}.
         *
         * @return a Mock MVC {@code ResultHandler} that will produce the documentation
         * @see org.springframework.test.web.servlet.MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
         * @see org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
         */
        public SwaggerResultHandler build() {
            if(StringUtils.isBlank(fileName)){
                fileName = "swagger.json";
            }
            return new SwaggerResultHandler(outputDir, fileName);
        }

        /**
         * Specifies the file name which should be used
         *
         * @param fileName the file name
         * @return the SwaggerResultHandler.Builder
         */
        public Builder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }
    }
}
