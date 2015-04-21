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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import java.io.IOException;
import java.util.Properties;


public class Swagger2MarkupResultHandler implements ResultHandler {

    private static final String OUTPUT_DIR = "io.springfox.staticdocs.outputDir";
    private static final Logger LOG = LoggerFactory.getLogger(Swagger2MarkupResultHandler.class);

    private final String outputDir;
    private final MarkupLanguage markupLanguage;
    private final String examplesFolderPath;

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
    public static Builder convertIntoFolder(String outputDir) {
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
            Properties properties = loadProperties();
            String baseOutputDir = properties.getProperty(OUTPUT_DIR);
            Validate.notEmpty(baseOutputDir, "Property '%s' must not be empty!", OUTPUT_DIR);
            return new Swagger2MarkupResultHandler(baseOutputDir + "/" + outputDir, markupLanguage,
                    examplesFolderPath);
        }

        /**
         * Loads properties from a {@code ClassPathResource} called documentation.properties.
         * If the file does not exist, the system properties are loaded.
         * @return the properties
         */
        private Properties loadProperties() {
            Resource resource = new ClassPathResource("/documentation.properties");
            Properties properties = new Properties();
            if (resource.exists()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("documentation.properties does exist in classpath.");
                }
                try {
                    properties = PropertiesLoaderUtils.loadProperties(resource);
                } catch (IOException e) {
                    LOG.error("Failed to load properties from documentation.properties", e);
                    properties.putAll(System.getProperties());
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("documentation.properties does not exist in classpath.");
                }
                properties.putAll(System.getProperties());
            }
            return properties;
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
