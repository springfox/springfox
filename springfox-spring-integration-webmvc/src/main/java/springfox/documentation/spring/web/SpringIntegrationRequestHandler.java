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
import com.fasterxml.classmate.TypeResolver;
import io.github.classgraph.utils.ReflectionUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.MediaType;
import org.springframework.integration.http.inbound.BaseHttpInboundEndpoint;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.UriTemplate;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * TODO: where does the WebMvcRequestHandler come from and can we avoid to make it for IntegrationRequestHandlerMapping?
 * TODO: also not make it for WebFluxIntegrationRequestHandlerMapping
 * <p>
 * <p>
 * The WebMvcRequestHandler is created by the WebMvcRequestHandlerProvider, which has a Component annotation.
 * <p>
 * Where is the model filled?
 * ApiDocumentationScanner
 * ApiListingReferenceScanner
 * makes RequestMappingContext groups by class name, contains OperationModelContextsBuilder and others
 * ApiListingReferenceScanResult
 * contains the groups, each group has RequestMappingContexts with OperationModelContextsBuilder and empty modelMap
 * ApiListingScanner#141: reads models
 * ApiDescriptionReader.read
 * DocumentationContext has ResourceGroupingStrategy
 * ApiOperationReader reads operations which contain parameters
 * <p>
 * ApiModelReader.read calls DocumentationPluginsManader.modelContexts calls (plugins) 1) OperationModelsProviderPlugin.apply calls OperationModelsProvider.collectParameters calls context.getParameters calls handler.getParameters is the SpringIntegrationRequestHandler's getParameters method.
 * ...calls 2) SwaggerOperationModelsProvider.apply calls collectFromApiOperation is about return types
 * finally (RequestMappingContext=context).operationModelContextsBuilder.build over all collected ModelContexts, i.e. the input and output params.
 * DefaultModelProvider.dependencies asks dependencyProvider for dependentModels
 * <p>
 * OperationBuilderPlugin in DocumentationPluginsManager.operation?
 */
public class SpringIntegrationRequestHandler implements RequestHandler {
    private static final String REQUEST_PARAMS_EXPRESSION_CONTEXT_VARIABLE = "#requestParams";
    private static final String PAYLOAD_EXPRESSION = "payloadExpression";
    private static final String HEADER_EXPRESSIONS = "headerExpressions";
    private final HandlerMethodResolver methodResolver; // springfox class
    private final RequestMappingInfo requestMapping; // TODO can be webflux, this is webmvc - use wrapper?
    private final HandlerMethod handlerMethod; // spring class, the handlerMethod holds HttpRequestHandler.handleRequest

    private final TypeResolver typeResolver = new TypeResolver();
    private final SpelExpressionParser parser = new SpelExpressionParser();

    public SpringIntegrationRequestHandler(
            HandlerMethodResolver methodResolver,
            RequestMappingInfo requestMapping,
            HandlerMethod handlerMethod) {
        this.methodResolver = methodResolver;
        this.requestMapping = requestMapping;
        this.handlerMethod = handlerMethod;
    }

    @Override
    public HandlerMethod getHandlerMethod() {
        return handlerMethod;
    }

    @Override
    public RequestHandler combine(RequestHandler other) {
        return this;
    }

    @Override
    public Class<?> declaringClass() {
        return handlerMethod.getBeanType();
    }

    @Override
    public boolean isAnnotatedWith(Class<? extends Annotation> annotation) {
        return null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation);
    }

    @Override
    public PatternsRequestCondition getPatternsCondition() {
        return new SpringIntegrationPatternsRequestConditionWrapper(requestMapping.getPatternsCondition());
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
    public Set<RequestMethod> supportedMethods() {
        return requestMapping.getMethodsCondition()
                .getMethods();
    }

    @Override
    public Set<? extends MediaType> produces() {
        return requestMapping.getProducesCondition()
                .getProducibleMediaTypes();
    }

    @Override
    public Set<? extends MediaType> consumes() {
        return requestMapping.getConsumesCondition()
                .getConsumableMediaTypes();
    }

    @Override
    public Set<NameValueExpression<String>> headers() {
        return SpringIntegrationNameValueExpressionWrapper.from(requestMapping.getHeadersCondition()
                .getExpressions());
    }

    @Override
    public Set<NameValueExpression<String>> params() {
        return SpringIntegrationNameValueExpressionWrapper.from(requestMapping.getParamsCondition()
                .getExpressions());
    }

    @Override
    public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
        // TODO handlerMethod is HttpRequestHandler.handleRequest - cannot annotate there
        return Optional.ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation));
    }

    @Override
    public RequestHandlerKey key() {
        return new RequestHandlerKey(
                requestMapping.getPatternsCondition()
                        .getPatterns(),
                requestMapping.getMethodsCondition()
                        .getMethods(),
                requestMapping.getConsumesCondition()
                        .getConsumableMediaTypes(),
                requestMapping.getProducesCondition()
                        .getProducibleMediaTypes());
    }

    @Override
    public springfox.documentation.spring.wrapper.RequestMappingInfo getRequestMapping() {
        throw new UnsupportedOperationException();
    }


    @Override
    // TODO can we synthesize a method parameter which does not exist, or does exist somewhere
    // TODO so we can add swagger annotations there?
    public List<ResolvedMethodParameter> getParameters() {
        // TODO webflux
        BaseHttpInboundEndpoint inboundEndpoint = (BaseHttpInboundEndpoint) handlerMethod.getBean();

        List<ResolvedMethodParameter> parameters = new ArrayList<>();

        addRequestBodyParam(inboundEndpoint, parameters);
        addPathVariableParams(parameters);
        addRequestParamParams(inboundEndpoint, parameters);

        return parameters;
    }

    private void addRequestBodyParam(BaseHttpInboundEndpoint inboundEndpoint,
                                     List<ResolvedMethodParameter> parameters) {
        ResolvableType requestPayloadType = (ResolvableType) ReflectionUtils.getFieldVal(inboundEndpoint,
                "requestPayloadType", true);
        if (requestPayloadType != null) {
            ResolvedType parameterType = typeResolver.resolve(requestPayloadType.getType());
            Map<String, Object> requestBodyAttributes = new HashMap<String, Object>();
            RequestBody requestBodyAnnotation =
                    AnnotationUtils.synthesizeAnnotation(requestBodyAttributes,
                            RequestBody.class, null);

            ResolvedMethodParameter body = new ResolvedMethodParameter(0, "body",
                    Arrays.asList(requestBodyAnnotation), parameterType);
            parameters.add(body);
        }
    }

    private void addPathVariableParams(List<ResolvedMethodParameter> parameters) {
        // TODO use streams:
        Set<String> patterns = requestMapping.getPatternsCondition()
                .getPatterns();
        for (String pattern : patterns) {
            UriTemplate uriTemplate = new UriTemplate(pattern);
            List<String> variableNames = uriTemplate.getVariableNames();
            for (String variableName : variableNames) {
                Map<String, Object> pathVariableAttributes = new HashMap<String, Object>();
                PathVariable pathVariableAnnotation =
                        AnnotationUtils.synthesizeAnnotation(pathVariableAttributes,
                                PathVariable.class, null);

                ResolvedMethodParameter param = new ResolvedMethodParameter(0, variableName,
                        Arrays.asList(pathVariableAnnotation), typeResolver.resolve(String.class));
                parameters.add(param);
            }
        }
    }


    private void addRequestParamParams(BaseHttpInboundEndpoint inboundEndpoint,
                                       List<ResolvedMethodParameter> parameters) {
        Expression payloadExpression = (Expression) ReflectionUtils.getFieldVal(inboundEndpoint,
                PAYLOAD_EXPRESSION, true);
        if (payloadExpression != null) {
            extractRequestParam(payloadExpression).ifPresent(
                    resolvedMethodParameter -> parameters.add(resolvedMethodParameter));
        }

        Map<String, Expression> headerExpressions = (Map<String, Expression>) ReflectionUtils.getFieldVal(
                inboundEndpoint, HEADER_EXPRESSIONS, true);

        if (headerExpressions != null) {
            for (Expression headerExpression : headerExpressions.values()) {
                extractRequestParam(headerExpression).ifPresent(resolvedMethodParameter ->
                        parameters.add(resolvedMethodParameter));
            }
        }
    }

    private Optional<ResolvedMethodParameter> extractRequestParam(Expression expression) {
        ResolvedMethodParameter ret = null;
        String expressionString = expression.getExpressionString();
        SpelExpression spelExpression = parser.parseRaw(expressionString);
        SpelNode ast = spelExpression.getAST();
        SpelNode firstChild = ast.getChild(0); // possible #requestParams, VariableReference
        if (firstChild != null && REQUEST_PARAMS_EXPRESSION_CONTEXT_VARIABLE.equals(firstChild.toStringAST())) {
            String firstIndexer = ast.getChild(1)
                    .toStringAST();// ['value'] or value, Indexer
            String requestParamName = firstIndexer.replaceAll("^\\['|'\\]", "");
            boolean required = requestParamName.equals(firstIndexer); // square brackets mean optional
            Map<String, Object> requestParamAttributes = new HashMap<String, Object>();
            requestParamAttributes.put("required", required);
            RequestParam requestParamAnnotation =
                    AnnotationUtils.synthesizeAnnotation(requestParamAttributes,
                            RequestParam.class, null);

            ret = new ResolvedMethodParameter(0, requestParamName,
                    Arrays.asList(requestParamAnnotation), typeResolver.resolve(String.class));
        }
        return Optional.ofNullable(ret);
    }

    @Override
    public ResolvedType getReturnType() {
        return methodResolver.methodReturnType(handlerMethod);
    }

    @Override
    public <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotation));
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SpringIntegrationRequestHandler.class.getSimpleName() + "{", "}")
                .add("requestMapping=" + requestMapping)
                .add("handlerMethod=" + handlerMethod)
                .add("key=" + key())
                .toString();
    }
}
