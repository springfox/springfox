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
package springfox.documentation.spi.service.contexts;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.RequestHandler;
import springfox.documentation.RequestHandlerKey;
import springfox.documentation.builders.ApiDescriptionBuilder;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.schema.GenericTypeNamingStrategy;
import springfox.documentation.spring.wrapper.NameValueExpression;
import springfox.documentation.spring.wrapper.PatternsRequestCondition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;

@SuppressWarnings("deprecation")
public class RequestMappingContext {
  private final OperationModelContextsBuilder operationModelContextsBuilder;
  private final DocumentationContext documentationContext;
  private final RequestHandler handler;
  private final String requestMappingPattern;
  private final ApiDescriptionBuilder apiDescriptionBuilder;

  private final Map<String, Set<springfox.documentation.schema.Model>> modelMap = new HashMap<>();

  public RequestMappingContext(
      String requestMappingId,
      DocumentationContext context,
      RequestHandler handler) {

    this.documentationContext = context;
    this.handler = handler;
    this.requestMappingPattern = "";
    this.operationModelContextsBuilder = new OperationModelContextsBuilder(
        context.getGroupName(),
        context.getDocumentationType(),
        requestMappingId,
        context.getAlternateTypeProvider(),
        context.getGenericsNamingStrategy(),
        context.getIgnorableParameterTypes());
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
  }

  private RequestMappingContext(
      DocumentationContext context,
      RequestHandler handler,
      OperationModelContextsBuilder operationModelContextsBuilder,
      String requestMappingPattern) {

    this.documentationContext = context;
    this.handler = handler;
    this.operationModelContextsBuilder = operationModelContextsBuilder;
    this.requestMappingPattern = requestMappingPattern;
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
  }

  private RequestMappingContext(
      DocumentationContext context,
      RequestHandler handler,
      OperationModelContextsBuilder operationModelContextsBuilder,
      String requestMappingPattern,
      Map<String, Set<springfox.documentation.schema.Model>> knownModels) {

    this.documentationContext = context;
    this.handler = handler;
    this.operationModelContextsBuilder = operationModelContextsBuilder;
    this.requestMappingPattern = requestMappingPattern;
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
    modelMap.putAll(knownModels);
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public String getRequestMappingPattern() {
    return requestMappingPattern;
  }

  public Map<String, Set<springfox.documentation.schema.Model>> getModelMap() {
    return unmodifiableMap(modelMap);
  }

  public OperationModelContextsBuilder operationModelsBuilder() {
    return operationModelContextsBuilder;
  }

  public ApiDescriptionBuilder apiDescriptionBuilder() {
    return apiDescriptionBuilder;
  }

  public ResolvedType alternateFor(ResolvedType resolvedType) {
    return documentationContext.getAlternateTypeProvider().alternateFor(resolvedType);
  }

  public Comparator<Operation> operationOrdering() {
    return documentationContext.operationOrdering();
  }

  public RequestMappingContext copyPatternUsing(String requestMappingPattern) {
    return new RequestMappingContext(
        documentationContext,
        handler,
        operationModelContextsBuilder,
        requestMappingPattern);
  }

  public RequestMappingContext withKnownModels(Map<String, Set<springfox.documentation.schema.Model>> knownModels) {
    return new RequestMappingContext(
        documentationContext,
        handler,
        operationModelContextsBuilder,
        requestMappingPattern,
        knownModels);
  }

  public Set<Class> getIgnorableParameterTypes() {
    return documentationContext.getIgnorableParameterTypes();
  }

  public GenericTypeNamingStrategy getGenericsNamingStrategy() {
    return documentationContext.getGenericsNamingStrategy();
  }

  public Set<ResolvedType> getAdditionalModels() {
    return documentationContext.getAdditionalModels().stream()
        .collect(collectingAndThen(
            toSet(),
            Collections::unmodifiableSet));
  }

  public PatternsRequestCondition getPatternsCondition() {
    return handler.getPatternsCondition();
  }

  public String getName() {
    return handler.getName();
  }

  public Set<RequestMethod> getMethodsCondition() {
    return handler.supportedMethods();
  }

  public Set<MediaType> produces() {
    return handler.produces();
  }

  public Set<MediaType> consumes() {
    return handler.consumes();
  }

  public Set<NameValueExpression<String>> headers() {
    return handler.headers();
  }

  public Set<NameValueExpression<String>> params() {
    return handler.params();
  }

  public String getGroupName() {
    return handler.groupName();
  }

  public List<ResolvedMethodParameter> getParameters() {
    return handler.getParameters();
  }

  public <T extends Annotation> Optional<T> findAnnotation(Class<T> annotation) {
    return handler.findAnnotation(annotation);
  }

  <T extends Annotation> Optional<T> findControllerAnnotation(Class<T> annotation) {
    return handler.findControllerAnnotation(annotation);
  }

  public <T extends Annotation> List<T> findAnnotations(Class<T> annotation) {
    List<T> annotations = new ArrayList<>();
    Optional<T> methodAnnotation = findAnnotation(annotation);
    Optional<T> controllerAnnotation = findControllerAnnotation(annotation);
    methodAnnotation.ifPresent(annotations::add);
    controllerAnnotation.ifPresent(annotations::add);
    return annotations;
  }

  public ResolvedType getReturnType() {
    return handler.getReturnType();
  }

  public RequestHandlerKey key() {
    return handler.key();
  }
}