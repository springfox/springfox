package com.mangofactory.swagger.core;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public interface ControllerResourceGroupingStrategy {
   public String getGroupCompatibleName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);
   public String getGroupName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);
//   public String getControllerPath(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);
}
