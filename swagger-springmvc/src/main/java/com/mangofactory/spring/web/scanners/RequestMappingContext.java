package com.mangofactory.spring.web.scanners;

import com.mangofactory.spring.web.plugins.DocumentationContext;
import com.mangofactory.spring.web.plugins.OperationModelsBuilder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RequestMappingContext  {
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final OperationModelsBuilder operationModelsBuilder;
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;

  private RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod,
                                OperationModelsBuilder operationModelsBuilder,
                                String requestMappingPattern) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.operationModelsBuilder = operationModelsBuilder;
    this.requestMappingPattern = requestMappingPattern;
  }

  public RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.requestMappingPattern = "";
    this.operationModelsBuilder = new OperationModelsBuilder(context.getDocumentationType());
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
    return new RequestMappingContext(documentationContext, requestMappingInfo, handlerMethod, operationModelsBuilder,
            requestMappingPattern);
  }

  public OperationModelsBuilder operationModelsBuilder() {
    return operationModelsBuilder;
  }

}
