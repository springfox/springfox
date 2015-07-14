/*
 *
 *  Copyright 2015 the original author or authors.
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import springfox.documentation.builders.ApiDescriptionBuilder;
import springfox.documentation.schema.Model;
import springfox.documentation.service.Operation;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class RequestMappingContext {
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final OperationModelContextsBuilder operationModelContextsBuilder;
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;
  private final ApiDescriptionBuilder apiDescriptionBuilder;

  private final Map<String, Model> modelMap = newHashMap();

  public RequestMappingContext(DocumentationContext context,
                               RequestMappingInfo requestMappingInfo,
                               HandlerMethod handlerMethod) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.requestMappingPattern = "";
    this.operationModelContextsBuilder = new OperationModelContextsBuilder(context.getDocumentationType(),
            context.getAlternateTypeProvider(),
            context.getGenericsNamingStrategy());
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
  }

  private RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod,
                                OperationModelContextsBuilder operationModelContextsBuilder,
                                String requestMappingPattern) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.operationModelContextsBuilder = operationModelContextsBuilder;
    this.requestMappingPattern = requestMappingPattern;
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
  }

  private RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod,
                                OperationModelContextsBuilder operationModelContextsBuilder,
                                String requestMappingPattern,
                                Map<String, Model> knownModels) {

    documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.operationModelContextsBuilder = operationModelContextsBuilder;
    this.requestMappingPattern = requestMappingPattern;
    this.apiDescriptionBuilder = new ApiDescriptionBuilder(documentationContext.operationOrdering());
    modelMap.putAll(knownModels);
  }

  public RequestMappingInfo getRequestMappingInfo() {
    return requestMappingInfo;
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public String getRequestMappingPattern() {
    return requestMappingPattern;
  }

  public ImmutableMap<String, Model> getModelMap() {
    return ImmutableMap.copyOf(modelMap);
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

  public Ordering<Operation> operationOrdering() {
    return documentationContext.operationOrdering();
  }

  public RequestMappingContext copyPatternUsing(String requestMappingPattern) {
    return new RequestMappingContext(documentationContext, requestMappingInfo, handlerMethod,
            operationModelContextsBuilder, requestMappingPattern);
  }

  public RequestMappingContext withKnownModels(Map<String, Model> knownModels) {
    return new RequestMappingContext(documentationContext, requestMappingInfo, handlerMethod,
            operationModelContextsBuilder, requestMappingPattern, knownModels);
  }
}
