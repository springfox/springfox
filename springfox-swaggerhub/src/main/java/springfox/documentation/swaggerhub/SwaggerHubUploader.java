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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

@Component
public class SwaggerHubUploader {

    private DocumentationCache documentationCache;

    private ServiceModelToSwagger2Mapper mapper;

    private SwaggerHubRestService swaggerHubRestService;

    @Autowired
    public SwaggerHubUploader(final DocumentationCache documentationCache, final ServiceModelToSwagger2Mapper mapper, final SwaggerHubRestService swaggerHubRestService) {
        this.documentationCache = documentationCache;
        this.mapper = mapper;
        this.swaggerHubRestService = swaggerHubRestService;
    }

}
