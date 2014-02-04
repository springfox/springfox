package com.mangofactory.swagger.mixins

import com.wordnik.swagger.model.Operation

import static com.mangofactory.swagger.ScalaUtils.emptyScalaList
import static com.mangofactory.swagger.ScalaUtils.toOption

class ApiOperationSupport {

   def operation(String responseClass) {
      scala.collection.immutable.List emptyList = emptyScalaList();
      Operation apiOperation = new Operation(

              "someMethod",
              "summary",
              "notes",
              responseClass,
              "nickname",
              1,
              emptyList,
              emptyList,
              emptyList,
              emptyList,
              emptyList,
              emptyList,
              toOption("false")
      )
      apiOperation
   }
}
