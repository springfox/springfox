package com.mangofactory.swagger.scanners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimaps;
import com.mangofactory.swagger.core.ControllerResourceNamingStrategy;
import com.wordnik.swagger.model.ApiListingReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import scala.actors.threadpool.Arrays;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.mangofactory.swagger.ScalaUtils.toOption;

@Slf4j
public class ApiListingReferenceScanner {
   private static final String REQUEST_MAPPINGS_EMPTY =
         "No RequestMappingHandlerMapping's found have you added <mvc:annotation-driven/>";
   private static final List DEFAULT_INCLUDE_PATTERNS = Arrays.asList(new String[]{"/**"});
   @Getter
   @Setter
   private List<RequestMappingHandlerMapping> requestMappingHandlerMapping;
   @Getter
   private List<ApiListingReference> apiListingReferences = newArrayList();

   private ArrayListMultimap<String, RequestMappingContext> resourceGroupRequestMappings = ArrayListMultimap.create();

   @Getter
   @Setter
   private String resourceGroup = "default";

   @Getter
   @Setter
   private List<Class<? extends Annotation>> excludeAnnotations;

   @Getter
   @Setter
   private ControllerResourceNamingStrategy controllerNamingStrategy;
   @Setter
   @Getter
   private List<String> includePatterns;

   public ApiListingReferenceScanner() {
      this.includePatterns = DEFAULT_INCLUDE_PATTERNS;
   }

   public ApiListingReferenceScanner(List<RequestMappingHandlerMapping> requestMappingHandlerMapping,
         ControllerResourceNamingStrategy controllerNamingStrategy) {
      Assert.notNull(requestMappingHandlerMapping, REQUEST_MAPPINGS_EMPTY);
      Assert.notEmpty(requestMappingHandlerMapping, REQUEST_MAPPINGS_EMPTY);
      Assert.notNull(controllerNamingStrategy, "controllerNamingStrategy is required");
      this.requestMappingHandlerMapping = requestMappingHandlerMapping;
      this.controllerNamingStrategy = controllerNamingStrategy;
      this.includePatterns = DEFAULT_INCLUDE_PATTERNS;
   }

   public List<ApiListingReference> scan() {
      log.info("Scanning for api listing references");
      scanSpringRequestMappings();

      return this.apiListingReferences;
   }

   public void scanSpringRequestMappings() {
      for (RequestMappingHandlerMapping requestMappingHandlerMapping : this.requestMappingHandlerMapping) {
         for (Entry<RequestMappingInfo, HandlerMethod> handlerMethodEntry :
               requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            RequestMappingInfo requestMappingInfo = handlerMethodEntry.getKey();
            HandlerMethod handlerMethod = handlerMethodEntry.getValue();
            if (shouldIncludeRequestMapping(requestMappingInfo, handlerMethod)) {
               String groupName = controllerNamingStrategy.getGroupName(requestMappingInfo, handlerMethod);
               resourceGroupRequestMappings.put(groupName, new RequestMappingContext(requestMappingInfo, handlerMethod));
            }
         }
      }

      int groupPosition = 0;
      for (String group : resourceGroupRequestMappings.keySet()) {
         String path = String.format("/%s/%s", this.resourceGroup, group);
         log.info(
               String.format(
                     "Create resource listing Path: %s Description: %s Psosition: %s", path, group,
                     groupPosition));
         this.apiListingReferences.add(new ApiListingReference(path, toOption(group), groupPosition));
         groupPosition++;
      }
   }

   private boolean requestMappingMatchesAnIncludePattern(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
      Set<String> patterns = patternsCondition.getPatterns();

      AntPathMatcher antPathMatcher = new AntPathMatcher();
      for (String path : patterns) {
         for (String includePattern : includePatterns) {
            Assert.notNull(includePattern, "Include patterns should never be null");
            if (antPathMatcher.match(includePattern, path)) {
               return true;
            }
         }
      }
      log.info(String.format("RequestMappingInfo did not match any include patterns: | %s", requestMappingInfo));
      return false;
   }

   private boolean shouldIncludeRequestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod) {
      return requestMappingMatchesAnIncludePattern(requestMappingInfo, handlerMethod)
            && !ignoreAnnotatedRequestMapping(handlerMethod);
   }

   public boolean ignoreAnnotatedRequestMapping(HandlerMethod handlerMethod) {
      if (null != excludeAnnotations) {
         for (Class<? extends Annotation> annotation : excludeAnnotations) {
            if (null != AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotation)) {
               log.info(String.format("Excluding method as it contains the excluded annotation: %s", annotation));
               return true;
            }
         }
         return false;
      }
      return false;
   }

   public Map<String, List<RequestMappingContext>> getResourceGroupRequestMappings() {
      return Multimaps.asMap(resourceGroupRequestMappings);
   }


}
