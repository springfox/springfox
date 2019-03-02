/*
 *
 *  Copyright 2016-2017 the original author or authors.
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
package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import nonapi.io.github.classgraph.utils.ReflectionUtils;
import org.springframework.core.ResolvableType;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriTemplate;
import springfox.documentation.service.ResolvedMethodParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.*;
import static org.springframework.core.annotation.AnnotationUtils.*;

/**
 * Provides information about Spring Integration inbound HTTP handlers.
 */
@Component
public class SpringIntegrationParametersProvider {
  private static final String REQUEST_PARAMS_EXPRESSION_CONTEXT_VARIABLE = "#requestParams";
  private static final String FIELD_PAYLOAD_EXPRESSION = "payloadExpression";
  private static final String FIELD_HEADER_EXPRESSIONS = "headerExpressions";

  private final SpelExpressionParser parser = new SpelExpressionParser();
  private final TypeResolver typeResolver = new TypeResolver();

  public List<ResolvedMethodParameter> getParameters(BaseHttpInboundEndpoint inboundEndpoint) {
    List<ResolvedMethodParameter> parameters = new ArrayList<>();
    parameters.addAll(addRequestBodyParam(inboundEndpoint));
    parameters.addAll(addPathVariableParams(inboundEndpoint));
    parameters.addAll(addRequestParamParams(inboundEndpoint));
    return parameters;
  }

  private List<ResolvedMethodParameter> addRequestBodyParam(BaseHttpInboundEndpoint inboundEndpoint) {
    List<ResolvedMethodParameter> parameters = new ArrayList<>();
    ResolvableType requestPayloadType = (ResolvableType) ReflectionUtils.getFieldVal(inboundEndpoint,
        "requestPayloadType", true);
    if (requestPayloadType != null) {
      ResolvedType parameterType = typeResolver.resolve(requestPayloadType.getType());
      Map<String, Object> requestBodyAttributes = new HashMap<>();
      RequestBody requestBodyAnnotation =
          synthesizeAnnotation(requestBodyAttributes,
              RequestBody.class, null);

      ResolvedMethodParameter body = new ResolvedMethodParameter(0, "body",
          singletonList(requestBodyAnnotation), parameterType);
      parameters.add(body);
    }
    return parameters;
  }

  private List<ResolvedMethodParameter> addPathVariableParams(BaseHttpInboundEndpoint inboundEndpoint) {
    return Stream.of(inboundEndpoint.getRequestMapping()
        .getPathPatterns())
        .map(pattern -> new UriTemplate(pattern).getVariableNames())
        .flatMap(Collection::stream)
        .map(variableName -> new ResolvedMethodParameter(0, variableName,
            singletonList(synthesizeAnnotation(emptyMap(), PathVariable.class, null)),
            typeResolver.resolve(String.class)))
        .collect(Collectors.toList());
  }

  private List<ResolvedMethodParameter> addRequestParamParams(BaseHttpInboundEndpoint inboundEndpoint) {
    List<ResolvedMethodParameter> parameters = new ArrayList<>();

    Expression payloadExpression = (Expression) ReflectionUtils.getFieldVal(inboundEndpoint,
        FIELD_PAYLOAD_EXPRESSION, true);
    if (payloadExpression != null) {
      extractRequestParam(payloadExpression, typeResolver).ifPresent(
          parameters::add);
    }
    @SuppressWarnings("unchecked")
    Map<String, Expression> headerExpressions = (Map<String, Expression>) ReflectionUtils.getFieldVal(
        inboundEndpoint, FIELD_HEADER_EXPRESSIONS, true);

    if (headerExpressions != null) {
      for (Expression headerExpression : headerExpressions.values()) {
        extractRequestParam(headerExpression, typeResolver).ifPresent(parameters::add);
      }
    }
    return parameters;
  }

  private Optional<ResolvedMethodParameter> extractRequestParam(Expression expression, TypeResolver typeResolver) {
    ResolvedMethodParameter ret = null;
    String expressionString = expression.getExpressionString();
    SpelExpression spelExpression = parser.parseRaw(expressionString);
    SpelNode ast = spelExpression.getAST();
    SpelNode firstChild = ast.getChild(0); // possible #requestParams, VariableReference
    if (firstChild != null && REQUEST_PARAMS_EXPRESSION_CONTEXT_VARIABLE.equals(firstChild.toStringAST())) {
      String firstIndexer = ast.getChild(1)
          .toStringAST(); // ['value'] or value, Indexer
      String requestParamName = firstIndexer.replaceAll("^\\['|']", "");
      boolean required = requestParamName.equals(firstIndexer); // square brackets mean optional
      Map<String, Object> requestParamAttributes = new HashMap<>();
      requestParamAttributes.put("required", required);
      RequestParam requestParamAnnotation =
          synthesizeAnnotation(requestParamAttributes,
              RequestParam.class, null);

      ret = new ResolvedMethodParameter(0, requestParamName,
          singletonList(requestParamAnnotation), typeResolver.resolve(String.class));
    }
    return Optional.ofNullable(ret);
  }

}
