package com.mangofactory.swagger.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

@Component
public class DefaultControllerResourceNamingStrategy implements ControllerResourceNamingStrategy {

   private final String relativeEndpointPrefix;
   private final String endpointSuffix;
   private  int skipPathCount;

   public DefaultControllerResourceNamingStrategy() {
      relativeEndpointPrefix = "/";
      endpointSuffix = "";
      this.skipPathCount = 0;
   }

   @Override
   public String getFirstGroupCompatibleName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
      Set<String> patterns = patternsCondition.getPatterns();
      String result = patterns.iterator().next();

      return getUriSafeRequestMappingPattern(result);
   }

   @Override
   public String getUriSafeRequestMappingPattern(String requestMappingPattern) {
      String result = requestMappingPattern.replaceFirst("/", "");
      //remove regex portion '/{businessId:\\w+}'
      result = result.replaceAll(":.*?}", "}");

      return result.isEmpty() ? "root" : result;
   }

   @Override
   public String getRequestPatternMappingEndpoint(String requestMappingPattern) {
      return relativeEndpointPrefix + getUriSafeRequestMappingPattern(requestMappingPattern) + endpointSuffix;
   }

   @Override
   public String getGroupName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getFirstGroupCompatibleName(requestMappingInfo, handlerMethod);
      group = StringUtils.removeStart(group, "/");
      String[] splits = group.split("/");
      int startingPoint = splits.length > skipPathCount ? skipPathCount : 0;
      group = splits[startingPoint];
      return group;
   }

   public int getSkipPathCount() {
      return skipPathCount;
   }

   public void setSkipPathCount(int skipPathCount) {
      this.skipPathCount = skipPathCount;
   }
}
