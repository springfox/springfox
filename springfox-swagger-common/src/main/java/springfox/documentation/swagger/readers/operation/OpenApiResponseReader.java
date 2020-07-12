/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.property.ModelSpecificationFactory;
import springfox.documentation.service.Header;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ResponseContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.*;
import static org.springframework.util.StringUtils.*;
import static springfox.documentation.swagger.annotations.Annotations.*;
import static springfox.documentation.swagger.readers.operation.ResponseHeaders.*;

@Component
//@Conditional(OasPresentCondition.class)
@Order(SwaggerPluginSupport.OAS_PLUGIN_ORDER)
public class OpenApiResponseReader implements OperationBuilderPlugin {

  private final TypeResolver typeResolver;
  private final ModelSpecificationFactory modelSpecifications;
  private final DocumentationPluginsManager documentationPlugins;

  @Autowired
  public OpenApiResponseReader(
      TypeResolver typeResolver,
      ModelSpecificationFactory modelSpecifications,
      DocumentationPluginsManager documentationPlugins) {
    this.typeResolver = typeResolver;
    this.modelSpecifications = modelSpecifications;
    this.documentationPlugins = documentationPlugins;
  }

  @Override
  public void apply(OperationContext context) {
    context.operationBuilder().responses(read(context));
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }


  @SuppressWarnings({
      "CyclomaticComplexity",
      "NPathComplexity"})
  protected Set<Response> read(OperationContext context) {
    ResolvedType defaultResponse = context.getReturnType();
    Optional<Operation> operationAnnotation = context.findAnnotation(Operation.class);
    Optional<ApiResponses> responsesAnnotation = context.findAnnotation(ApiResponses.class);
    Optional<ApiResponse> responseAnnotation = context.findAnnotation(ApiResponse.class);
    List<ApiResponse> allApiResponses
        = Stream.concat(
        operationAnnotation.map(fromOperationAnnotation())
            .orElse(new ArrayList<>()).stream(),
        responsesAnnotation.map(fromApiResponsesAnnotation())
            .orElse(new ArrayList<>()).stream())
        .collect(Collectors.toList());
    responseAnnotation.ifPresent(allApiResponses::add);


    Set<Response> responses = new HashSet<>();
    for (ApiResponse apiResponse : allApiResponses) {
      Map<String, Header> headers = new HashMap<>();
      List<Example> examples = new ArrayList<>();
      Optional<ResolvedType> type;
      ResponseContext responseContext = new ResponseContext(
          context.getDocumentationContext(),
          context);
      for (Content each : apiResponse.content()) {
        type = resolvedType(each.schema());
        if (isSuccessful(apiResponse.responseCode())) {
          type = type.map(Optional::of).orElse(Optional.of(defaultResponse));
        }
        if (!type.isPresent()) {
          continue;
        }
        ModelContext modelContext = context.operationModelsBuilder()
            .addReturn(
                typeResolver.resolve(each.schema().implementation()),
                Optional.empty());
        for (ExampleObject eachExample : each.examples()) {
          if (!isEmpty(eachExample.value())) {
            examples.add(new ExampleBuilder()
                .mediaType(each.mediaType())
                .description(eachExample.description())
                .summary(eachExample.summary())
                .id(eachExample.name())
                .value(eachExample.value()).build());
          }
        }
        headers.putAll(headers(apiResponse.headers()));

        type.ifPresent(t -> responseContext.responseBuilder()
            .representation(each.mediaType().isEmpty() ? MediaType.ALL : MediaType.valueOf(each.mediaType()))
            .apply(r -> r.model(
                m -> m.copyOf(modelSpecifications.create(modelContext, t)))));
      }
      responseContext.responseBuilder()
          .examples(examples)
          .description(apiResponse.description())
          .headers(headers.values())
          .code(apiResponse.responseCode());
      responses.add(documentationPlugins.response(responseContext));
    }
    return responses;
  }

  static boolean isSuccessful(String code) {
    try {
      return HttpStatus.Series.SUCCESSFUL.equals(HttpStatus.Series.valueOf(code));
    } catch (Exception ignored) {
      return false;
    }
  }

  private Optional<ResolvedType> resolvedType(
      Schema apiResponse) {
    return ofNullable(resolvedTypeFromSchema(
        typeResolver,
        null).apply(apiResponse));
  }
}
