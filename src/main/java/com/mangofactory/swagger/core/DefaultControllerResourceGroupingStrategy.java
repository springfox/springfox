package com.mangofactory.swagger.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

public class DefaultControllerResourceGroupingStrategy implements ControllerResourceGroupingStrategy {

   public DefaultControllerResourceGroupingStrategy() {
   }

   @Override
   public String getGroupCompatibleName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
      Set<String> patterns = patternsCondition.getPatterns();
      String result = patterns.iterator().next();

      result = result.replaceFirst("/", "");
      //remove regex portion '/{businessId:\\w+}'
      result = result.replaceAll(":.*?}", "}");

      result = StringUtils.replaceEach(result, new String[]{"{", "}"}, new String[]{"(", ")"});
      return result;
   }

   @Override
   public String getGroupName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getGroupCompatibleName(requestMappingInfo, handlerMethod);
      group = StringUtils.removeStart(group, "/");
      String[] splits = group.split("/");
      group = splits.length > 0 ? splits[0] : "root";
      return group;
   }
//
//   @Override
//   public String getControllerPath(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
//      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
//      Set<String> patterns = patternsCondition.getPatterns();
//      String firstPattern = patterns.iterator().next();
//      return pathRoot(firstPattern);
//   }
}
