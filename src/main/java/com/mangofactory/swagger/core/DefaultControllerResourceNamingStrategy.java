package com.mangofactory.swagger.core;

import com.wordnik.swagger.annotations.Api;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

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
      String groupName = getFirstGroupCompatibleName(requestMappingInfo, handlerMethod);
      if (null != handlerMethod) {
         Class<?> controllerClass = handlerMethod.getBeanType();
         Api apiAnnotation = controllerClass.getAnnotation(Api.class);
         if (null != apiAnnotation && !StringUtils.isBlank(apiAnnotation.value())) {
            groupName = apiAnnotation.value();
         }

         List<String> allMappings = getAllRequestMappingsFromControllerClass(controllerClass);

         if (null != allMappings && allMappings.size() > 0) {
            String longestCommonPath = longestCommonPath(allMappings.toArray(new String[allMappings.size()]));
            if (!StringUtils.isBlank(longestCommonPath) && !longestCommonPath.endsWith("/")) {
               groupName = longestCommonPath;
            }
         }

      }
      return cleanAndSkip(groupName);
   }

   private List<String> getAllRequestMappingsFromControllerClass(Class<?> controllerClass) {

      List<String> allMappings = newArrayList();
      RequestMapping classLevelMapping = controllerClass.getAnnotation(RequestMapping.class);
      addMappingIfPresent(allMappings, classLevelMapping);

      Method[] declaredMethods = controllerClass.getDeclaredMethods();

      if (null != declaredMethods) {
         for (Method method : declaredMethods) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            addMappingIfPresent(allMappings, annotation);
         }
      }
      return allMappings;
   }

   private void addMappingIfPresent(List<String> allMappings, RequestMapping classLevelMapping) {
      if (null != classLevelMapping) {
         String[] mappings = classLevelMapping.value();
         if (null != mappings && mappings.length > 0) {
            for (String mapping : mappings) {
               allMappings.add(getUriSafeRequestMappingPattern(mapping));
            }
         }
      }
   }

   private String longestCommonPath(String... paths) {
      String commonPath = "";
      String[][] folders = new String[paths.length][];

      for (int i = 0; i < paths.length; i++) {
         folders[i] = paths[i].split("/");
      }

      for (int j = 0; j < folders[0].length; j++) {
         String s = folders[0][j];
         for (int i = 1; i < paths.length; i++) {
            if (!s.equals(folders[i][j])){
               return commonPath;
            }
         }
         commonPath += s + "/";
      }
      return commonPath;
   }

   private String cleanAndSkip(String groupName){
      int ordinalIndex = StringUtils.ordinalIndexOf(groupName, "/", skipPathCount + 1);

      if(ordinalIndex > 0){
         groupName = groupName.substring(ordinalIndex);
      }
      groupName = StringUtils.removeStart(groupName, "/");
      groupName = groupName.replaceAll("/", "_");
      return groupName.isEmpty() ? "root" : groupName;
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
