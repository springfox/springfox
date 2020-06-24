/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spring.web.dummy;

import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bug2219ListingScanner implements ApiListingScannerPlugin {

  @Override
  public List<ApiDescription> apply(DocumentationContext context) {
    return new ArrayList<>(
        Arrays.asList(
            new ApiDescription(
                "different-group",
                "/bugs/2219",
                "This is a bug summary",
                "This is a bug-fix for 2219",
                Collections.singletonList(
                    new OperationBuilder(
                        new CachingOperationNameGenerator())
                        .authorizations(new ArrayList<>())
                        .codegenMethodNameStem("bug2219GET")
                        .method(HttpMethod.GET)
                        .notes("This is a test method")
                        .requestParameters(
                            Collections.singletonList(
                                new RequestParameterBuilder()
                                    .description("description of bug 2219")
                                    .name("description")
                                    .in(ParameterType.QUERY)
                                    .required(true)
                                    .query(q -> new ModelSpecificationBuilder()
                                        .scalarModel(ScalarType.STRING)
                                        .build())
                                    .build()))
                        .build()),
                false)));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }

}
