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
package springfox.test.contract.swagger;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Collections.*;

@SuppressWarnings({
    "WhitespaceAround",
    "ParenPad",
    "deprecation"})
public class Bug1767ListingScanner implements ApiListingScannerPlugin {

  // tag::api-listing-plugin[]
  private final CachingOperationNameGenerator operationNames;

  /**
   * @param operationNames - CachingOperationNameGenerator is a component bean
   *                       that is available to be autowired
   */
  public Bug1767ListingScanner(CachingOperationNameGenerator operationNames) {//<9>
    this.operationNames = operationNames;
  }

  @Override
  public List<ApiDescription> apply(DocumentationContext context) {
    return new ArrayList<>(
        Arrays.asList( //<1>
            new ApiDescription(
                "test",
                "/bugs/1767",
                "This is a bug summary",
                "This is a bug",
                Collections.singletonList( //<2>
                    new OperationBuilder(operationNames)
                        .authorizations(new ArrayList<>())
                        .codegenMethodNameStem("bug1767GET") //<3>
                        .method(HttpMethod.GET)
                        .notes("This is a test method")
                        .parameters(
                            Collections.singletonList( //<4>
                                new springfox.documentation.builders.ParameterBuilder()
                                    .description(
                                        "search by "
                                            + "description")
                                    .type(new TypeResolver()
                                        .resolve(String.class))
                                    .name("description")
                                    .parameterType("query")
                                    .parameterAccess("access")
                                    .required(true)
                                    .modelRef(new springfox.documentation.schema.ModelRef(
                                        "string")) //<5>
                                    .build()))
                        .requestParameters(
                            Collections.singletonList( //<4a>
                                new RequestParameterBuilder()
                                    .description("search by description")
                                    .name("description")
                                    .required(true)
                                    .in(ParameterType.QUERY)
                                    .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING))) //<5b>
                                    .build()))
                        .responses(responseMessages()) //<6>
                        .responseModel(new springfox.documentation.schema.ModelRef("string")) //<7>
                        .responses(responses()) //<6b>
                        .build()),
                false),
            new ApiDescription(
                "different-group",
                //<8>
                "/different/2219",
                "This is a bug summary",
                "This is a bug",
                Collections.singletonList(
                    new OperationBuilder(
                        operationNames)
                        .authorizations(new ArrayList<>())
                        .codegenMethodNameStem("bug2219GET")
                        .method(HttpMethod.GET)
                        .notes("This is a test method")
                        .parameters(
                            Collections.singletonList(
                                new springfox.documentation.builders.ParameterBuilder()
                                    .description("description of bug 2219")
                                    .type(new TypeResolver().resolve(String.class))
                                    .name("description")
                                    .parameterType("query")
                                    .parameterAccess("access")
                                    .required(true)
                                    .modelRef(new springfox.documentation.schema.ModelRef("string"))
                                    .build()))
                        .requestParameters(
                            Collections.singletonList(
                                new RequestParameterBuilder()
                                    .description("description of bug 2219")
                                    .name("description")
                                    .in("query")
                                    .required(true)
                                    .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                    .build()))
                        .responses(responseMessages())
                        .responseModel(new springfox.documentation.schema.ModelRef("string"))
                        .build()),
                false)));
  }

  /**
   * @return Set of response messages that overide the default/global response messages
   */
  private Set<Response> responseMessages() { //<8>
    return singleton(new springfox.documentation.builders.ResponseBuilder()
        .code("200")
        .description("Successfully received bug 1767 or 2219 response")
        .representation(MediaType.TEXT_PLAIN)
        .apply(r -> r.model(m -> m.scalarModel(ScalarType.STRING)))
        .build());
  }

  /**
   * @return Set of response messages that overide the default/global response messages
   */
  private Collection<Response> responses() { //<8b>
    return singletonList(new ResponseBuilder()
        .code("200")
        .description("Successfully received bug 1767 or 2219 response")
        .representation(MediaType.ALL)
        .apply(r -> r.model(m -> m.scalarModel(ScalarType.STRING))
            .build())
        .build());
  }
  // end::api-listing-plugin[]

  @Override
  public boolean supports(DocumentationType delimiter) {
    return DocumentationType.SWAGGER_2.equals(delimiter);
  }

}
