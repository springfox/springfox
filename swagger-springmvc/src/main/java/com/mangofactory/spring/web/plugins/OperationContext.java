package com.mangofactory.spring.web.plugins;

import com.mangofactory.schema.plugins.DocumentationType;
import com.mangofactory.service.model.ResponseMessage;
import com.mangofactory.service.model.builder.OperationBuilder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;

public class OperationContext {
  private final OperationBuilder operationBuilder;
  private final RequestMethod requestMethod;
  private final HandlerMethod handlerMethod;
  private final int operationIndex;
  private final RequestMappingInfo requestMappingInfo;
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;

  public OperationContext(OperationBuilder operationBuilder, RequestMethod requestMethod, HandlerMethod
          handlerMethod, int operationIndex, RequestMappingInfo requestMappingInfo, DocumentationContext
          documentationContext, String requestMappingPattern) {
    this.operationBuilder = operationBuilder;
    this.requestMethod = requestMethod;
    this.handlerMethod = handlerMethod;
    this.operationIndex = operationIndex;
    this.requestMappingInfo = requestMappingInfo;
    this.documentationContext = documentationContext;
    this.requestMappingPattern = requestMappingPattern;
  }

  public OperationBuilder operationBuilder() {
    return operationBuilder;
  }

  public String httpMethod() {
    return requestMethod.toString();
  }

  public HandlerMethod getHandlerMethod() {
    return handlerMethod;
  }

  public int operationIndex() {
    return operationIndex;
  }


  public List<ResponseMessage> getGlobalResponseMessages(String forHttpMethod) {
    return documentationContext.getGlobalResponseMessages().get(RequestMethod.valueOf(forHttpMethod));
  }

  public AuthorizationContext authorizationContext() {
    return documentationContext.getAuthorizationContext();
  }

  public String requestMappingPattern() {
    return requestMappingPattern;
  }

  public RequestMappingInfo getRequestMappingInfo() {
    return requestMappingInfo;
  }

  public DocumentationContext getDocumentationContext() {
    return documentationContext;
  }

  public DocumentationType getDocumentationType() {
    return documentationContext.getDocumentationType();
  }
}
