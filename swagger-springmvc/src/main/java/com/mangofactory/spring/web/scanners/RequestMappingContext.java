package com.mangofactory.spring.web.scanners;

import com.mangofactory.service.model.Model;
import com.mangofactory.spring.web.plugins.DocumentationContext;
import com.mangofactory.spring.web.readers.CommandContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

import static com.google.common.collect.Maps.*;

public class RequestMappingContext implements CommandContext<Map<String, Object>> {
  private final RequestMappingInfo requestMappingInfo;
  private final HandlerMethod handlerMethod;
  private final Map<String, Object> context = newHashMap();
  private final Map<String, Model> modelMap = newHashMap();
  private final DocumentationContext documentationContext;
  private final String requestMappingPattern;

  private RequestMappingContext(DocumentationContext context,
                                 RequestMappingInfo requestMappingInfo,
                                 HandlerMethod handlerMethod,
                                 String requestMappingPattern) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.requestMappingPattern = requestMappingPattern;
  }

  public RequestMappingContext(DocumentationContext context,
                                RequestMappingInfo requestMappingInfo,
                                HandlerMethod handlerMethod) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
    this.requestMappingPattern = "";
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

  public Map<String, Model> getModelMap() {
    return modelMap;
  }

  public String getRequestMappingPattern() {
    return requestMappingPattern;
  }

  public RequestMappingContext copyPatternUsing(String requestMappingPattern) {
    return new RequestMappingContext(documentationContext, requestMappingInfo, handlerMethod, requestMappingPattern);
  }
}
