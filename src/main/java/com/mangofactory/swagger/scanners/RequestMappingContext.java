package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.core.CommandContext;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Getter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class RequestMappingContext implements CommandContext<Map<String, Object>> {
   @Getter
   private final RequestMappingInfo requestMappingInfo;
   @Getter
   private final HandlerMethod handlerMethod;

   @Getter
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
}
