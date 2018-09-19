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

import io.swagger.models.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.uploader.FileUploader;

import java.util.Map;

/**
 * Implementation of {@link FileUploader} interface to upload Swagger files to SwaggerHub.
 *
 * @author Esteban Cristóbal Rodríguez
 */
@Component
public class SwaggerHubFileUploader implements FileUploader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerHubFileUploader.class);

    private static final String SWAGGER_HUB_URL = "https://api.swaggerhub.com/apis/{owner}/{api}";

    private ServiceModelToSwagger2Mapper mapper;

    private JsonSerializer jsonSerializer;

    private RestTemplate restTemplate;

    @Value("${springfox.documentation.uploader.api.key}")
    private String swaggerHubApiKey;

    @Value("${springfox.documentation.uploader.owner}")
    private String swaggerHubOwner;

    @Autowired
    public SwaggerHubFileUploader(
            final ServiceModelToSwagger2Mapper mapper,
            final JsonSerializer jsonSerializer, final RestTemplate restTemplate) {
        this.mapper = mapper;
        this.jsonSerializer = jsonSerializer;
        this.restTemplate = restTemplate;
    }

    @Override
    public void uploadSwaggerDescriptors(final Map<String, Documentation> descriptors) {
        LOGGER.info("Start Swagger file upload to SwaggerHub");
        for (Map.Entry<String, Documentation> entry : descriptors.entrySet()) {
            final String apiName = entry.getKey();
            final Swagger swagger = this.mapper.mapDocumentation(entry.getValue());
            this.uploadFile(apiName, swagger);
        }
        LOGGER.info("Finished Swagger file upload to SwaggerHub.");
    }

    private void uploadFile(final String apiName, final Swagger swagger) {
        final Json jsonSwagger = this.jsonSerializer.toJson(swagger);
        LOGGER.debug("Uploading Swagger file for {} endpoint", apiName);
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic ".concat(this.swaggerHubApiKey));
        final HttpEntity<Json> request = new HttpEntity<Json>(jsonSwagger, headers);
        final ResponseEntity<String> response = this.restTemplate
                .postForEntity(SWAGGER_HUB_URL, request, String.class, this.swaggerHubOwner, apiName);
        switch (response.getStatusCode()) {
            case OK:
                // Empty because the behaviour is going to be the same that in CREATED case
            case CREATED:
                LOGGER.debug("Successfully uploaded API {}", apiName);
                break;
            case RESET_CONTENT:
                LOGGER.warn("Successfully uploaded API {}, but it should be reloaded", apiName);
                break;
            case BAD_REQUEST:
                LOGGER.error("Invalid generated Swagger file for API {}", apiName);
                break;
            case FORBIDDEN:
                LOGGER.error("Reached maximum number of APIs allowed into SwaggerHub");
                break;
            case CONFLICT:
                LOGGER.error("Could not overwrite current version for API {}", apiName);
                break;
            case UNSUPPORTED_MEDIA_TYPE:
                LOGGER.error("Incorrect Content-Type value");
                break;
            default:
                LOGGER.error("Could not upload API to SwaggerHub. Received HTTP Status code {}", response.getStatusCode());
                break;
        }
        LOGGER.debug("Successfully uploaded Swagger file for {} endpoint", apiName);
    }
}
