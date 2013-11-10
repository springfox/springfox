package com.mangofactory.swagger.controllers;

import com.mangofactory.swagger.core.SwaggerApiResourceListing;
import com.wordnik.swagger.model.ResourceListing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class DefaultSwaggerController {

   @Getter
   @Setter
   private Map<String, SwaggerApiResourceListing> swaggerApiResourceListingMap;

   @RequestMapping(value = {"/api-docs", "/api-docs/{resourceKey}"}, method = RequestMethod.GET)
   public
   @ResponseBody
   ResourceListing getResourceListing(String resourceKey) {
      return getSwaggerResourceListing(resourceKey);
   }

   public ResourceListing getSwaggerResourceListing(String resourceKey) {
      Assert.notNull(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is null");
      Assert.notEmpty(swaggerApiResourceListingMap, "swaggerApiResourceListingMap is empty");
      if (null == resourceKey) {
         return swaggerApiResourceListingMap.values().iterator().next().getResourceListing();
      }
      return swaggerApiResourceListingMap.get(resourceKey).getResourceListing();
   }
}
