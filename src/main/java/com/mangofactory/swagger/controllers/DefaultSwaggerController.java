package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.wordnik.swagger.model.ResourceListing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DefaultSwaggerController {

   @Getter
   @Setter
   @Autowired
   private Map<String, SwaggerApiResourceListing> swaggerApiResourceListingMap;

   @RequestMapping(value = {"/api-docs/{resourceKey}"}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResponseEntity<ResourceListing> getResourceListing(@PathVariable String resourceKey) {
      return getSwaggerResourceListing(resourceKey);
   }

   @RequestMapping(value = {"/api-docs"}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResponseEntity<ResourceListing> getResourceListingByKey() {
      return getSwaggerResourceListing(null);
   }


   public ResponseEntity<ResourceListing> getSwaggerResourceListing(String resourceKey) {
      Assert.notNull(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is null");
      Assert.notEmpty(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is empty");
      ResourceListing resourceListing = null;
      HttpStatus status = HttpStatus.NOT_FOUND;
      if (null == resourceKey) {
         resourceListing = swaggerApiResourceListingMap.values().iterator().next().getResourceListing();
      } else{
         if(swaggerApiResourceListingMap.containsKey(resourceKey)){
            resourceListing = swaggerApiResourceListingMap.get(resourceKey).getResourceListing();
         }
      }
      if(null != resourceListing){
         status = HttpStatus.OK;
      }
      return new ResponseEntity<ResourceListing>(resourceListing, status);
   }
}
