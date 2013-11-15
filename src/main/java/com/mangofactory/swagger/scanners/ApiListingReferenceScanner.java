package com.mangofactory.swagger.scanners;

import com.mangofactory.swagger.core.ControllerResourceGroupingStrategy;
import com.wordnik.swagger.model.ApiListingReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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
   private String pathPrefix = "api-docs";

   @Getter
   @Setter
   private String pathSuffix = "";

   @Getter
   @Setter
   private List<Class<Annotation>> excludeAnnotations;

   @Getter
   @Setter
   private ControllerResourceGroupingStrategy controllerNamingStrategy;

   public ApiListingReferenceScanner() {
   }

   public ApiListingReferenceScanner(List<RequestMappingHandlerMapping> handlerMappings,
         ControllerResourceGroupingStrategy controllerNamingStrategy) {
      Assert.notNull(handlerMappings, REQUEST_MAPPINGS_EMPTY);
      Assert.notEmpty(handlerMappings, REQUEST_MAPPINGS_EMPTY);
      Assert.notNull(controllerNamingStrategy, "controllerNamingStrategy is required");
      this.handlerMappings = handlerMappings;
      this.controllerNamingStrategy = controllerNamingStrategy;
   }

   public List<ApiListingReference> scan() {
      scanForDefaultSpringResources();

      return this.apiListingReferences;
   }

   public void scanForDefaultSpringResources() {
      Set<String> resourceGroups = new LinkedHashSet<String>();
      for (RequestMappingHandlerMapping requestMappingHandlerMapping : handlerMappings) {
         for (Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry :
               requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo requestMappingInfo = handlerMethodEntry.getKey();
            HandlerMethod handlerMethod = handlerMethodEntry.getValue();
            if (!shouldIgnoreRequestMapping(requestMappingInfo, handlerMethod)) {
               resourceGroups.add(controllerNamingStrategy.getGroupName(requestMappingInfo, handlerMethod));
            }
         }
      }

      int groupPosition = 0;
      for(String group : resourceGroups ){
        String path = String.format("/%s/%s%s", this.pathPrefix, group, this.pathSuffix);
         this.apiListingReferences.add(new ApiListingReference(path, toOption(group), groupPosition));
         groupPosition++;
      }
   }

   private boolean shouldIgnoreRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return ignoreAnnotatedRequestMapping(handlerMethod)
          || ignoreRequestMapping(requestMappingInfo, handlerMethod);
   }

   public boolean ignoreAnnotatedRequestMapping(HandlerMethod handlerMethod) {
      if(null != excludeAnnotations){
         for(Class<Annotation> annotation : excludeAnnotations){
            if(null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation)){
               log.info(String.format("Excluding method as it contains the excluded annotation: %s", annotation));
               return  true;
            }
         }
         return false;
      }
      return false;
   }

   /**
    * Override this method to ignore a particular request mapping
    *
    * @param requestMappingInfo
    * @param handlerMethod
    * @return whether or not to ignore the request mapping
    */
   public boolean ignoreRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return false;
   }


}
