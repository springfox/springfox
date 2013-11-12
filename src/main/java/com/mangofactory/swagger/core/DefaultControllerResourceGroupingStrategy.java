package com.mangofactory.swagger.core;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

import static com.mangofactory.swagger.core.ControllerNamingUtils.firstSlashPortion;

public class DefaultControllerResourceGroupingStrategy implements ControllerResourceGroupingStrategy {

   public DefaultControllerResourceGroupingStrategy() {
   }

   @Override
   public String getControllerName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      if(null != handlerMethod){
         return handlerMethod.getMethod().getName();
      }
      return "unknown";
   }

   @Override
   public String getControllerGroup(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
      Set<String> patterns = patternsCondition.getPatterns();
      String firstPattern = patterns.iterator().next();
      return firstSlashPortion(firstPattern);
   }

   @Override
   public String getControllerPath(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }
}
