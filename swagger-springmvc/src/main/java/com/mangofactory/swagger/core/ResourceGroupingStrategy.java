package com.mangofactory.swagger.core;

import com.mangofactory.swagger.scanners.ResourceGroup;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.Set;

public interface ResourceGroupingStrategy {
  /**
   * Gets the resource group for a particular request mapping.
   * Typically several requestMappings will live under a particular resource group.
   *
   * @param requestMappingInfo request mapping info
   * @param handlerMethod      handler method
   * @return Resource group uris.
   * @see <code>ApiListingReferenceScanner</code>
   */
  public Set<ResourceGroup> getResourceGroups(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);


  /**
   * Gets the resource description. i.e. the top level resource name for a set of api operations as displayed on the
   * main swagger ui page. e.g. 'BusinessApiController'
   * This is typically the class name of the spring controller or value() attribute of any @Api annotations on that
   * controller class
   *
   * @param requestMappingInfo request mapping info
   * @param handlerMethod      handler method
   * @return description of the resource
   */
  public String getResourceDescription(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

  /**
   * Gets the position of the resource. Typically com.wordnik.swagger.annotations.Api.position
   * @param requestMappingInfo
   * @param handlerMethod
   * @return The numeric position
   */
  public Integer getResourcePosition(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod);

}
