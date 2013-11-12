package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.DefaultControllerResourceGroupingStrategy;
import com.wordnik.swagger.model.ApiListingReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map.Entry;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

@Slf4j
public class ApiListingReferenceScanner {
   private static final String REQUEST_MAPPINGS_EMPTY =
       "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>";
   @Getter
   @Setter
   private List<RequestMappingHandlerMapping> handlerMappings;
   @Getter
   private List<ApiListingReference> apiListingReferences = newArrayList();

   @Getter
   @Setter
   DefaultControllerResourceGroupingStrategy defaultControllerNamingStrategy;


   public ApiListingReferenceScanner(List<RequestMappingHandlerMapping> handlerMappings) {
      Assert.notNull(handlerMappings, REQUEST_MAPPINGS_EMPTY);
      Assert.notEmpty(handlerMappings, REQUEST_MAPPINGS_EMPTY);
      this.handlerMappings = handlerMappings;
   }

   public List<ApiListingReference> scan() {
      scanForSpringHandlerMethods();

      return this.apiListingReferences;
   }

   public void scanForSpringHandlerMethods() {
      for (RequestMappingHandlerMapping requestMappingHandlerMapping : handlerMappings) {
         for (Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry :
             requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo requestMappingInfo = handlerMethodEntry.getKey();
            HandlerMethod handlerMethod = handlerMethodEntry.getValue();

            if (!templateIgnoreRequestMapping(requestMappingInfo, handlerMethod)) {

            }

            int position = 0;
            new ApiListingReference("path", toOption("description"), 0);
         }
      }
   }

   private boolean templateIgnoreRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return !ignoreAnnotatedRequestMapping(requestMappingInfo, handlerMethod)
          && !ignoreRequestMapping(requestMappingInfo, handlerMethod);
   }

   public boolean ignoreAnnotatedRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      ApiIgnore ignore = handlerMethod.getMethodAnnotation(ApiIgnore.class);
      return null != ignore;
   }

   /**
    * Override this to ignore a particular request mapping
    *
    * @param requestMappingInfo
    * @param handlerMethod
    * @return whether or not to ignore the request mapping
    */
   public boolean ignoreRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return false;
   }


}
