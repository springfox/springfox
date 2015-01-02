package com.mangofactory.swagger.mixins

import com.mangofactory.service.model.ApiListing
import com.mangofactory.service.model.ApiListingReference
import com.mangofactory.service.model.OAuth
import com.mangofactory.service.model.ResourceListing

class ApiListingSupport {

   def apiListing(authorizations = [], models = null) {
      new ApiListing(
              "1.0"
              ,
              "",
              "/relative-path-to-endpoint",
              [],
              [],
              [],
              authorizations,
              [],
              models,
              null,
              1)
   }

   def apiListingReference() {
      new ApiListingReference("/path", "description", 3)
   }

   def resourceListing(List<OAuth> authorizationTypes) {
      new ResourceListing(
              "apiVersion"
              ,
              [apiListingReference()],
              authorizationTypes,
              null)
   }
}
