package com.mangofactory.swagger.mixins

import com.wordnik.swagger.core.SwaggerSpec
import com.wordnik.swagger.model.ApiListing

import static com.mangofactory.swagger.ScalaUtils.toOption
import static com.mangofactory.swagger.ScalaUtils.toScalaList

class ApiListingSupport {

   def apiListing(){
      scala.collection.immutable.List emptyList = toScalaList(null);
      ApiListing apiListing = new ApiListing(
              "1.0",
              SwaggerSpec.version(),
              "",
              "/relative-path-to-endpoint",
              emptyList,
              emptyList,
              emptyList,
              emptyList,
              emptyList,
              toOption(null),
              toOption(null),
              1);
   }
}
