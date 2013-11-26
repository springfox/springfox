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

   public DefaultControllerResourceNamingStrategy() {
      relativeEndpointPrefix = "/";
      endpointSuffix = "";
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

      result = StringUtils.replaceEach(result, new String[]{"{", "}"}, new String[]{"(", ")"});
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
      group = splits[0];
      return group;
   }
}
