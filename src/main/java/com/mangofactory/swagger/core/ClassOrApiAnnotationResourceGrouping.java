package com.mangofactory.swagger.core;

import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class ClassOrApiAnnotationResourceGrouping implements ResourceGroupingStrategy {
   private static final Logger log = LoggerFactory.getLogger(ClassOrApiAnnotationResourceGrouping.class);

   public ClassOrApiAnnotationResourceGrouping() {
   }


   @Override
   public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return getClassOrApiAnnotationValue(handlerMethod);
   }

   @Override
   public String getResourceGroupPath(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getClassOrApiAnnotationValue(handlerMethod).replaceAll("\\.", "_");
      try {
         group = URLEncoder.encode(group, "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
         log.error("Could not encode group", e);
      }
      return group;
   }

   private String getClassOrApiAnnotationValue(HandlerMethod handlerMethod) {
      Class<?> controllerClass = handlerMethod.getBeanType();
      String group = controllerClass.getCanonicalName();

      Api apiAnnotation = controllerClass.getAnnotation(Api.class);
      if (null != apiAnnotation && !isBlank(apiAnnotation.value())) {
         group = apiAnnotation.value();
      }
      return group;
   }
}
