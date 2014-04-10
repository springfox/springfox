package com.mangofactory.swagger.core;

import com.google.common.base.Optional;
import com.mangofactory.swagger.scanners.ResourceGroup;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Sets.*;
import static org.apache.commons.lang.StringUtils.*;

@Component
public class ClassOrApiAnnotationResourceGrouping implements ResourceGroupingStrategy {
   private static final Logger log = LoggerFactory.getLogger(ClassOrApiAnnotationResourceGrouping.class);

   public ClassOrApiAnnotationResourceGrouping() {
   }


   @Override
   public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      Class<?> controllerClass = handlerMethod.getBeanType();
      String group = controllerClass.getCanonicalName();
      
      Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
      if (null != apiAnnotation) {
          String descriptionFromAnnotation = Optional.fromNullable(emptyToNull(apiAnnotation.description()))
                  .or(apiAnnotation.value());
          if (!isNullOrEmpty(descriptionFromAnnotation)) {
              return descriptionFromAnnotation;
          }
      }
      
      return group;
   }

   @Override
   public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      String group = getClassOrApiAnnotationValue(handlerMethod).replaceAll("\\.", "_");
      try {
         group = URLEncoder.encode(group, "ISO-8859-1");
      } catch (UnsupportedEncodingException e) {
         log.error("Could not encode group", e);
      }
      return newHashSet(new ResourceGroup(group));
   }

   private String getClassOrApiAnnotationValue(HandlerMethod handlerMethod) {
      Class<?> controllerClass = handlerMethod.getBeanType();
      String group = controllerClass.getCanonicalName();

      Api apiAnnotation = AnnotationUtils.findAnnotation(controllerClass, Api.class);
      if (null != apiAnnotation && !isBlank(apiAnnotation.value())) {
         group = apiAnnotation.value();
      }
      return group;
   }
}
