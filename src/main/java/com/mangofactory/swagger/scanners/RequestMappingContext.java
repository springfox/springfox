package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.core.CommandContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class RequestMappingContext implements CommandContext<Map<String, Object>> {
   private final RequestMappingInfo requestMappingInfo;
   private final HandlerMethod handlerMethod;
   private Map<String, Object> context = newHashMap();

   public RequestMappingContext(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
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

   public ApiOperation getApiOperationAnnotation() {
      return this.handlerMethod.getMethodAnnotation(ApiOperation.class);
   }

   public ApiParam getApiParamAnnotation(){
      return this.handlerMethod.getMethodAnnotation(ApiParam.class);
   }

   public RequestMappingInfo getRequestMappingInfo() {
      return requestMappingInfo;
   }

   public HandlerMethod getHandlerMethod() {
      return handlerMethod;
   }

   public Map<String, Object> getContext() {
      return context;
   }
}
