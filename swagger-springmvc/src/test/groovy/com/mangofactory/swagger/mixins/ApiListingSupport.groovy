package com.mangofactory.swagger.mixins
import com.mangofactory.swagger.models.servicemodel.ApiListing
import com.mangofactory.swagger.models.servicemodel.ApiListingReference
import com.mangofactory.swagger.models.servicemodel.OAuth
import com.mangofactory.swagger.models.servicemodel.ResourceListing


class ApiListingSupport {

   def apiListing(authorizations = [], models = null) {
      new ApiListing(
              "1.0",
              "1.2",
              "",
              "/relative-path-to-endpoint",
              [],
              [],
              [],
              authorizations,
              [],
              models,
              null,
              1);
   }

   def apiListingReference() {
      new ApiListingReference("/path", "description", 3)
   }

   def resourceListing(List<OAuth> authorizationTypes) {
      new ResourceListing(
              "apiVersion",
              "swagger version",
              [apiListingReference()],
              authorizationTypes,
              null)
   }
}
