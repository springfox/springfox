package springdox.documentation.spring.web.mixins

import springdox.documentation.service.ApiListing
import springdox.documentation.service.ApiListingReference
import springdox.documentation.service.OAuth
import springdox.documentation.service.ResourceListing

class ApiListingSupport {

   def apiListing(authorizations = [], models = null) {
      new ApiListing(
              "1.0"
              ,
              "",
              "/relative-path-to-endpoint",
              [] as Set,
              [] as Set,
              [] as Set,
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
