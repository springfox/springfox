package com.mangofactory.swagger.core;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public interface ResourceGroupingStrategy {
   /**
    * Gets the resource group for a particular request mapping.
    * Typically several requestMappings will live under a particular resource group.
    * @see <code><ApiListingReferenceScanner/code>
    * @param requestMappingInfo
    * @param handlerMethod
    * @return A resource group uri.
    */
   public String getResourceGroupPath(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);


   /**
    * Gets the resource description. i.e. the top level resource name for a set of api operations as displayed on the
    * main swagger ui page. e.g. 'BusinessApiController'
    * This is typically the class name of the spring controller or value() attribute of any @Api annotations on that
    * controller class
    * @param handlerMethod
    * @param requestMappingInfo
    * @return
    */
   public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);
}
