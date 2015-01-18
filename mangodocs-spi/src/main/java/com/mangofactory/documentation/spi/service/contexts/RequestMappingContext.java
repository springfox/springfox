package com.mangofactory.documentation.spi.service.contexts;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RequestMappingContext  {
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final OperationModelContextsBuilder operationModelContextsBuilder;
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;

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
  }

  public RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.requestMappingPattern = "";
    this.operationModelContextsBuilder = new OperationModelContextsBuilder(context.getDocumentationType(),
            context.getAlternateTypeProvider());
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

  public RequestMappingContext copyPatternUsing(String requestMappingPattern) {
    return new RequestMappingContext(documentationContext, requestMappingInfo, handlerMethod, operationModelContextsBuilder,
            requestMappingPattern);
  }

  public OperationModelContextsBuilder operationModelsBuilder() {
    return operationModelContextsBuilder;
  }

  public ResolvedType alternateFor(ResolvedType resolvedType) {
    return documentationContext.getAlternateTypeProvider().alternateFor(resolvedType);
  }
}
