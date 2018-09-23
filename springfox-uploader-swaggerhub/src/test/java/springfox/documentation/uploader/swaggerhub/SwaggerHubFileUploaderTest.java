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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import springfox.documentation.uploader.FileUploaderBeanConfiguration;
import springfox.documentation.uploader.swaggerhub.spring.FileUploaderTestBeanConfiguration;

import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link SwaggerHubFileUploader}.
 *
 * @author Esteban Cristóbal Rodríguez
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FileUploaderBeanConfiguration.class, FileUploaderTestBeanConfiguration.class})
public class SwaggerHubFileUploaderTest {

    @Autowired
    private SwaggerHubFileUploader fileUploader;

    @Autowired
    private MockRestServiceServer server;

    @Test
    public void testRunOk() {
        assertTrue(false);
    }

    @Test
    public void testRunCreated() {
        assertTrue(false);
    }

    @Test
    public void testRunResetContent() {
        assertTrue(false);
    }

    @Test
    public void testRunBadRequest() {
        assertTrue(false);
    }

    @Test
    public void testRunForbidden() {
        assertTrue(false);
    }

    @Test
    public void testRunConflict() {
        assertTrue(false);
    }

    @Test
    public void testRunUnsupportedMediaType() {
        assertTrue(false);
    }

    @Test
    public void testRunDefault() {
        assertTrue(false);
    }
}
