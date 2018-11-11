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
package springfox.documentation.spring.web;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.plugins.SpringIntegrationParametersProvider;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Provides information about WebMvc based Spring Integration inbound HTTP handlers.
 */
public class SpringIntegrationWebMvcRequestHandler extends WebMvcRequestHandler {
    private final HandlerMethodResolver methodResolver;
    private final HandlerMethod handlerMethod;
    private SpringIntegrationParametersProvider parametersProvider;

    public SpringIntegrationWebMvcRequestHandler(
            String contextPath,
            HandlerMethodResolver methodResolver,
            RequestMappingInfo requestMapping,
            HandlerMethod handlerMethod,
            SpringIntegrationParametersProvider parametersProvider) {
        super(contextPath, methodResolver, requestMapping, handlerMethod);

        this.methodResolver = methodResolver;
        this.handlerMethod = handlerMethod;
        this.parametersProvider = parametersProvider;
    }

    @Override
    public String groupName() {
        // TODO come up with a better group name, instead of generic handler class name
        //   maybe the defining class of a flow in DSL? - but what about xml?
        return ControllerNamingUtils.controllerNameAsGroup(handlerMethod);
    }

    @Override
    public String getName() {
        // TODO method name behind URL - maybe the id of the gateway?
        return handlerMethod.getMethod()
                .getName();
    }

    @Override
    public ResolvedType getReturnType() {
        // TODO come up with a way to define this
        return methodResolver.methodReturnType(handlerMethod);
    }

    @Override
    public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
        // TODO see if we can synthesize the annotations usually requested here
        return ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation));
    }

    @Override
    public List<ResolvedMethodParameter> getParameters() {

        BaseHttpInboundEndpoint inboundEndpoint = (BaseHttpInboundEndpoint) handlerMethod.getBean();
        return parametersProvider.getParameters(inboundEndpoint);
//        List<ResolvedMethodParameter> parameters = new ArrayList<>();
//        addRequestBodyParam(inboundEndpoint, parameters);
//        addPathVariableParams(parameters);
//        addRequestParamParams(inboundEndpoint, parameters);
//        return parameters;
    }

//    private void addRequestBodyParam(BaseHttpInboundEndpoint inboundEndpoint,
//                                     List<ResolvedMethodParameter> parameters) {
//        ResolvableType requestPayloadType = (ResolvableType) ReflectionUtils.getFieldVal(inboundEndpoint,
//                "requestPayloadType", true);
//        if (requestPayloadType != null) {
//            ResolvedType parameterType = typeResolver.resolve(requestPayloadType.getType());
//            Map<String, Object> requestBodyAttributes = new HashMap<>();
//            RequestBody requestBodyAnnotation =
//                    AnnotationUtils.synthesizeAnnotation(requestBodyAttributes,
//                            RequestBody.class, null);
//
//            ResolvedMethodParameter body = new ResolvedMethodParameter(0, "body",
//                    Collections.singletonList(requestBodyAnnotation), parameterType);
//            parameters.add(body);
//        }
//    }
//
//    private void addPathVariableParams(List<ResolvedMethodParameter> parameters) {
//
//
//        // TODO use streams:
//        Set<String> patterns = requestMapping.getPatternsCondition()
//                .getPatterns();
//        for (String pattern : patterns) {
//            UriTemplate uriTemplate = new UriTemplate(pattern);
//            List<String> variableNames = uriTemplate.getVariableNames();
//            for (String variableName : variableNames) {
//                Map<String, Object> pathVariableAttributes = new HashMap<>();
//                PathVariable pathVariableAnnotation =
//                        AnnotationUtils.synthesizeAnnotation(pathVariableAttributes,
//                                PathVariable.class, null);
//
//                ResolvedMethodParameter param = new ResolvedMethodParameter(0, variableName,
//                        Collections.singletonList(pathVariableAnnotation), typeResolver.resolve(String.class));
//                parameters.add(param);
//            }
//        }
//    }
//
//
//    private void addRequestParamParams(BaseHttpInboundEndpoint inboundEndpoint,
//                                       List<ResolvedMethodParameter> parameters) {
//        Expression payloadExpression = (Expression) ReflectionUtils.getFieldVal(inboundEndpoint,
//                PAYLOAD_EXPRESSION, true);
//        if (payloadExpression != null) {
//            extractRequestParam(payloadExpression).ifPresent(
//                    parameters::add);
//        }
//
//        @SuppressWarnings("unchecked")
//        Map<String, Expression> headerExpressions = (Map<String, Expression>) ReflectionUtils.getFieldVal(
//                inboundEndpoint, HEADER_EXPRESSIONS, true);
//
//        if (headerExpressions != null) {
//            for (Expression headerExpression : headerExpressions.values()) {
//                extractRequestParam(headerExpression).ifPresent(parameters::add);
//            }
//        }
//    }
//
//    private Optional<ResolvedMethodParameter> extractRequestParam(Expression expression) {
//        ResolvedMethodParameter ret = null;
//        String expressionString = expression.getExpressionString();
//        SpelExpression spelExpression = parser.parseRaw(expressionString);
//        SpelNode ast = spelExpression.getAST();
//        SpelNode firstChild = ast.getChild(0); // possible #requestParams, VariableReference
//        if (firstChild != null && REQUEST_PARAMS_EXPRESSION_CONTEXT_VARIABLE.equals(firstChild.toStringAST())) {
//            String firstIndexer = ast.getChild(1)
//                    .toStringAST();// ['value'] or value, Indexer
//            String requestParamName = firstIndexer.replaceAll("^\\['|']", "");
//            boolean required = requestParamName.equals(firstIndexer); // square brackets mean optional
//            Map<String, Object> requestParamAttributes = new HashMap<>();
//            requestParamAttributes.put("required", required);
//            RequestParam requestParamAnnotation =
//                    AnnotationUtils.synthesizeAnnotation(requestParamAttributes,
//                            RequestParam.class, null);
//
//            ret = new ResolvedMethodParameter(0, requestParamName,
//                    Collections.singletonList(requestParamAnnotation), typeResolver.resolve(String.class));
//        }
//        return Optional.ofNullable(ret);
//    }

}
