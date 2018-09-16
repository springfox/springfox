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
package springfox.documentation.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.DocumentationCache;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ApiUploaderLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiUploaderLauncher.class);

    private DocumentationCache documentationCache;

    private List<SwaggerHubFileUploader> swaggerHubFileUploaders;

    @Autowired
    public ApiUploaderLauncher(
            final DocumentationCache documentationCache) {
        this.documentationCache = documentationCache;
    }

    @PostConstruct
    public void init() {
        for (SwaggerHubFileUploader fileUploader : this.swaggerHubFileUploaders) {
            fileUploader.uploadSwaggerDescriptors(this.documentationCache.all());
        }
    }

}
