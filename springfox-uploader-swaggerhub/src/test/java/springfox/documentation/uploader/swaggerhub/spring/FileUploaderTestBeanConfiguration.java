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
package springfox.documentation.uploader.swaggerhub.spring;

import io.swagger.models.Swagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.DocumentationBuilder;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class FileUploaderTestBeanConfiguration {

    @Bean
    public ServiceModelToSwagger2Mapper mapper() {
        final ServiceModelToSwagger2Mapper mapper = mock(ServiceModelToSwagger2Mapper.class);
        final Swagger swagger = new Swagger();
        when(mapper.mapDocumentation(any(Documentation.class))).thenReturn(swagger);
        return mapper;
    }

    @Bean
    public JsonSerializer serializer() {
        final JsonSerializer serializer = mock(JsonSerializer.class);
        final Json json = new Json("{ \"key\" : \"value\" }");
        when(serializer.toJson(anyObject())).thenReturn(json);
        return serializer;
    }

    @Bean
    public DocumentationCache documentationCache() {
        final DocumentationCache documentationCache = mock(DocumentationCache.class);
        final Map<String, Documentation> documentationMap = new HashMap<String, Documentation>(1);
        final Documentation documentation = new DocumentationBuilder()
                .basePath("base:uri")
                .consumes(new HashSet<String>(Arrays.asList("application/json")))
                .name("doc-group")
                .host("test")
                .schemes(new HashSet<String>(Arrays.asList("https")))
                .tags(new HashSet<Tag>(Arrays.asList(new Tag("tag", "tag description"))))
                .build();
        documentationMap.put("test", documentation);
        when(documentationCache.all()).thenReturn(documentationMap);
        return documentationCache;
    }

    @Bean
    public MockRestServiceServer server(final RestTemplate restTemplate) {
        return MockRestServiceServer.bindTo(restTemplate).build();
    }
}
