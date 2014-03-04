package com.mangofactory.swagger.mixins
import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiListing
import com.wordnik.swagger.model.OAuth
import com.wordnik.swagger.model.ResourceListing

import static com.mangofactory.swagger.ScalaUtils.*

class ApiListingSupport {

   def apiListing(authorizations = emptyScalaList(), models = toOption(null)) {
      scala.collection.immutable.List emptyList = toScalaList(null);
      new ApiListing(
              "1.0",
              SwaggerSpec.version(),
              "",
              "/relative-path-to-endpoint",
              emptyList,
              emptyList,
              emptyList,
              authorizations,
              emptyList,
              models,
              toOption(null),
              1);
   }

   def resourceListing(List<OAuth> authorizationTypes) {
      new ResourceListing(
              "apiVersion",
              "swagger version",
              emptyScalaList(),
              toScalaList(authorizationTypes),
              toOption(null))
   }
}
