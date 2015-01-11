package com.mangofactory.spring.web.scanners;

import com.mangofactory.service.model.Model;
import com.mangofactory.spring.web.plugins.DocumentationContext;
import com.mangofactory.spring.web.plugins.OperationModelsBuilder;
import com.mangofactory.spring.web.readers.CommandContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class RequestMappingContext implements CommandContext<Map<String, Object>> {
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final Map<String, Object> context = newHashMap();
  private final OperationModelsBuilder operationModelsBuilder;
  private final Map<String, Model> modelMap = newHashMap();
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

  public Object get(String lookupKey) {
    return context.get(lookupKey);
  }

  public void put(String key, Object object) {
    context.put(key, object);
  }

  @Override
  public Map<String, Object> getResult() {
    return context;
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

  public Map<String, Model> getModelMap() {
    return modelMap;
  }

  public void setModelMap(Map<String, Model> modelMap) {
    this.modelMap.putAll(modelMap);
  }
}
