package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.mangofactory.swagger.core.SwaggerCache;
import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DefaultSwaggerController {

   public static final String DOCUMENTATION_BASE_PATH = "/api-docs";

   @Autowired
   private SwaggerCache swaggerCache;

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
      ResponseEntity<ApiListing> responseEntity = new ResponseEntity<ApiListing>(HttpStatus.NOT_FOUND);
      Map<String, ApiListing> apiListingMap = swaggerCache.getSwaggerApiListingMap().get(resourceKey);
      if (null != apiListingMap) {
         ApiListing apiListing = apiListingMap.get(resource);
         if (null != apiListing) {
            responseEntity = new ResponseEntity<ApiListing>(apiListing, HttpStatus.OK);
         }
      }
      return responseEntity;
   }

   private ResponseEntity<ResourceListing> getSwaggerResourceListing(String resourceKey) {
      ResponseEntity<ResourceListing> responseEntity = new ResponseEntity<ResourceListing>(HttpStatus.NOT_FOUND);
      ResourceListing resourceListing = null;

      if (null == resourceKey) {
         resourceListing = swaggerCache.getSwaggerApiResourceListingMap().values().iterator().next();
      } else {
         if (swaggerCache.getSwaggerApiResourceListingMap().containsKey(resourceKey)) {
            resourceListing = swaggerCache.getSwaggerApiResourceListingMap().get(resourceKey);
         }
      }
      if (null != resourceListing) {
         responseEntity = new ResponseEntity<ResourceListing>(resourceListing, HttpStatus.OK);
      }
      return responseEntity;
   }
}
