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

import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;


public class Swagger2MarkupResultHandler implements ResultHandler {

    private final String outputDir;

    Swagger2MarkupResultHandler(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Apply the action on the given result.
     *
     * @param result the result of the executed request
     * @throws Exception if a failure occurs
     */
    @Override
    public void handle(MvcResult result) throws Exception {
        String swaggerJson = result.getResponse().getContentAsString();
        Swagger2MarkupConverter.fromString(swaggerJson).build().intoFolder(outputDir);
    }
}
