package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class DefaultSwaggerController {

   public static final String DOCUMENTATION_BASE_PATH = "/api-docs";
   @Getter
   @Setter
   @Resource(name = "swaggerApiResourceListingMap")
   private Map<String, SwaggerApiResourceListing> swaggerApiResourceListingMap;

   @Getter
   @Setter
   @Resource(name = "swaggerApiListings")
   private Map<String, Map<String, ApiListing>> swaggerApiListings;

   @ApiIgnore
   @RequestMapping(value = {DOCUMENTATION_BASE_PATH}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResponseEntity<ResourceListing> getResourceListingByKey() {
      return getSwaggerResourceListing(null);
   }

   @ApiIgnore
   @RequestMapping(value = {DOCUMENTATION_BASE_PATH + "/{resourceKey}"}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResponseEntity<ResourceListing> getResourceListing(@PathVariable String resourceKey) {
      return getSwaggerResourceListing(resourceKey);
   }

   @ApiIgnore
   @RequestMapping(value = {DOCUMENTATION_BASE_PATH + "/{resourceKey}/{resource}"}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResponseEntity<ApiListing> getApiListing(@PathVariable String resourceKey, @PathVariable String resource) {
      return getSwaggerApiListing(resourceKey, resource);
   }

   private ResponseEntity<ApiListing> getSwaggerApiListing(String resourceKey, String resource) {
      Assert.notNull(swaggerApiListings, "swaggerApiListings is null");
      Assert.notEmpty(swaggerApiListings, "swaggerApiListings is empty");

      ResponseEntity<ApiListing> responseEntity = new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
      Map<String, ApiListing> apiListingMap = swaggerApiListings.get(resourceKey);
      if (null != apiListingMap) {
         ApiListing apiListing = apiListingMap.get(resource);
         if (null != apiListing) {
            responseEntity = new ResponseEntity<ApiListing>(apiListing, HttpStatus.OK);
         }
      }
      return responseEntity;
   }

   private ResponseEntity<ResourceListing> getSwaggerResourceListing(String resourceKey) {
      Assert.notNull(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is null");
      Assert.notEmpty(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is empty");

      ResponseEntity<ResourceListing> responseEntity = new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
      ResourceListing resourceListing = null;

      if (null == resourceKey) {
         resourceListing = swaggerApiResourceListingMap.values().iterator().next().getResourceListing();
      } else {
         if (swaggerApiResourceListingMap.containsKey(resourceKey)) {
            resourceListing = swaggerApiResourceListingMap.get(resourceKey).getResourceListing();
         }
      }
      if (null != resourceListing) {
         responseEntity = new ResponseEntity<ResourceListing>(resourceListing, HttpStatus.OK);
      }
      return responseEntity;
   }
}
