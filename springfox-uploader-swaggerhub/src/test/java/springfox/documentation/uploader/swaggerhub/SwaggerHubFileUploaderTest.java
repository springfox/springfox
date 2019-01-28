/*
 *
 *  Copyright 2018 the original author or authors.
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
package springfox.documentation.uploader.swaggerhub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.uploader.FileUploaderException;
import springfox.documentation.uploader.spring.FileUploaderBeanConfiguration;
import springfox.documentation.uploader.swaggerhub.spring.FileUploaderTestBeanConfiguration;

import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

/**
 * Test class for {@link SwaggerHubFileUploader}.
 *
 * @author Esteban Cristóbal Rodríguez
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FileUploaderBeanConfiguration.class, FileUploaderTestBeanConfiguration.class})
@TestPropertySource(properties = {"springfox.documentation.uploader.api.key=api", "springfox.documentation.uploader.owner=owner"})
public class SwaggerHubFileUploaderTest {

    private static final String SWAGGERHUB_URL = "https://api.swaggerhub.com/apis/owner/test";

    @Autowired
    private DocumentationCache documentationCache;

    @Autowired
    private SwaggerHubFileUploader fileUploader;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testRunOk() throws FileUploaderException {
        this.configureMockServer(HttpStatus.OK);
        this.runFileUploader();
    }

    @Test
    public void testRunCreated() throws FileUploaderException {
        this.configureMockServer(HttpStatus.CREATED);
        this.runFileUploader();
    }

    @Test
    public void testRunResetContent() throws FileUploaderException {
        this.configureMockServer(HttpStatus.RESET_CONTENT);
        this.runFileUploader();
    }

    @Test
    public void testRunBadRequest() throws FileUploaderException {
        this.configureMockServer(HttpStatus.BAD_REQUEST);
        this.runFileUploader();
    }

    @Test
    public void testRunForbidden() throws FileUploaderException {
        this.configureMockServer(HttpStatus.FORBIDDEN);
        this.runFileUploader();
    }

    @Test
    public void testRunConflict() throws FileUploaderException {
        this.configureMockServer(HttpStatus.CONFLICT);
        this.runFileUploader();
    }

    @Test
    public void testRunUnsupportedMediaType() throws FileUploaderException {
        this.configureMockServer(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        this.runFileUploader();
    }

    @Test(expected = FileUploaderException.class)
    public void testRunDefault() throws FileUploaderException {
        this.configureMockServer(HttpStatus.MULTI_STATUS);
        this.runFileUploader();
    }

    private void configureMockServer(final HttpStatus httpStatus) {
        this.server.reset();
        this.server.expect(once(), requestTo(SWAGGERHUB_URL)).andRespond(withStatus(httpStatus));
    }

    private void runFileUploader() throws FileUploaderException {
        this.fileUploader.uploadSwaggerDescriptors(this.documentationCache.all());
        this.server.verify();
    }
}
