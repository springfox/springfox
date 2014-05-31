package com.mangofactory.swagger.core;

import com.mangofactory.swagger.scanners.ResourceGroup;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.mangofactory.swagger.core.StringUtils.splitCamelCase;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class ClassOrApiAnnotationResourceGrouping implements ResourceGroupingStrategy {
   private static final Logger log = LoggerFactory.getLogger(ClassOrApiAnnotationResourceGrouping.class);

   public ClassOrApiAnnotationResourceGrouping() {
   }

   @Override
   public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getClassOrApiAnnotationValue(handlerMethod);
      return group;
   }

   @Override
   public Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      Class<?> controllerClass = handlerMethod.getBeanType();
      Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
      if (null != apiAnnotation && !isBlank(apiAnnotation.value())) {
         return apiAnnotation.position();
      }
      return 0;
   }

   @Override
   public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getClassOrApiAnnotationValue(handlerMethod).toLowerCase().replaceAll(" ", "-");
      Integer position = getResourcePosition(requestMappingInfo, handlerMethod);
      return newHashSet(new ResourceGroup(group.toLowerCase(), position));
   }

   private String getClassOrApiAnnotationValue(HandlerMethod handlerMethod) {
      Class<?> controllerClass = handlerMethod.getBeanType();
      String group = splitCamelCase(controllerClass.getSimpleName(), " ");

      Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
      if (null != apiAnnotation && !isBlank(apiAnnotation.value())) {
         group = apiAnnotation.value();
      }
      return group;
   }
}
