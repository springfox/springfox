package com.mangofactory.swagger.core;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

@Component
public class DefaultControllerResourceNamingStrategy implements ControllerResourceNamingStrategy {

   private String endpointSuffix;
   private int skipPathCount;

   public DefaultControllerResourceNamingStrategy() {
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
      String result = requestMappingPattern;
      //remove regex portion '/{businessId:\\w+}'
      result = result.replaceAll(":.*?}", "}");

      return result.isEmpty() ? "/" : result;
   }

   @Override
   public String getRequestPatternMappingEndpoint(String requestMappingPattern) {
      String endpoint = getUriSafeRequestMappingPattern(requestMappingPattern) + endpointSuffix;
      return endpoint.replaceAll("//", "/");
   }

   @Override
   public String getGroupName(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getFirstGroupCompatibleName(requestMappingInfo, handlerMethod);

      if (null != handlerMethod) {
         Class<?> aClass = handlerMethod.getBeanType();
         Api apiAnnotation = aClass.getAnnotation(Api.class);
         if (null != apiAnnotation && !StringUtils.isBlank(apiAnnotation.value())) {
            return apiAnnotation.value();
         }
      }

      group = StringUtils.removeStart(group, "/");
      String[] splits = group.split("/");
      int startingPoint = splits.length > skipPathCount ? skipPathCount : 0;
      group = splits[startingPoint];
      return group.isEmpty() ? "root" : group;
   }

   public String getEndpointSuffix() {
      return endpointSuffix;
   }

   public void setEndpointSuffix(String endpointSuffix) {
      this.endpointSuffix = endpointSuffix;
   }

   public int getSkipPathCount() {
      return skipPathCount;
   }

   public void setSkipPathCount(int skipPathCount) {
      this.skipPathCount = skipPathCount;
   }
}
