package com.mangofactory.swagger.scanners;

import com.mangofactory.service.model.Model;
import com.mangofactory.springmvc.plugins.DocumentationContext;
import com.mangofactory.swagger.core.CommandContext;
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

  public RequestMappingContext(DocumentationContext context,
                               RequestMappingInfo requestMappingInfo,
                               HandlerMethod handlerMethod) {

    this.documentationContext = context;
    this.requestMappingInfo = requestMappingInfo;
    this.handlerMethod = handlerMethod;
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

  public RequestMappingContext newCopyUsingHandlerMethod(HandlerMethod handlerMethod) {
    return new RequestMappingContext(this.documentationContext, this.requestMappingInfo, handlerMethod);
  }

  public RequestMappingContext newCopy() {
    return new RequestMappingContext(this.documentationContext, this.requestMappingInfo, handlerMethod);
  }
}
