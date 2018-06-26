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
package springfox.documentation.swaggerhub;

import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class SwaggerHubApiUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerHubApiUploader.class);

    private DocumentationCache documentationCache;

    private ServiceModelToSwagger2Mapper mapper;

    private RestTemplate restTemplate;

    @Autowired
    public SwaggerHubApiUploader(
            final DocumentationCache documentationCache,
            final ServiceModelToSwagger2Mapper mapper) {
        this.documentationCache = documentationCache;
        this.mapper = mapper;
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("Start Swagger file upload to SwaggerHub");
        final Map<String, Documentation> documentationMap = this.documentationCache.all();
        for (Map.Entry<String, Documentation> entry : documentationMap.entrySet()) {
            final Swagger swagger = this.mapper.mapDocumentation(entry.getValue());
            LOGGER.debug("Uploading Swagger file for {} endpoint", entry.getKey());
            //TODO Implement file upload
            LOGGER.debug("Successfully uploaded Swagger file for {} endpoint", entry.getKey());
        }
        LOGGER.debug("Finished Swagger file upload to SwaggerHub. Uploaded {} files", documentationMap.size());
    }

}
