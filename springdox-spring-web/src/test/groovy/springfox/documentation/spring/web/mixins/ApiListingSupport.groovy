package springfox.documentation.spring.web.mixins

import springfox.documentation.service.ApiListing
import springfox.documentation.service.OAuth
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.ResourceListing

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
