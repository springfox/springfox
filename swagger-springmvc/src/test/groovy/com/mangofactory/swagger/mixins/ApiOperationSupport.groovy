package com.mangofactory.swagger.mixins

import com.wordnik.swagger.model.Operation

import static com.mangofactory.swagger.ScalaUtils.emptyScalaList
import static com.mangofactory.swagger.ScalaUtils.toOption

class ApiOperationSupport {

  def operation(int position = 0, String method = "someMethod") {
    scala.collection.immutable.List emptyList = emptyScalaList();
    new Operation(
          method,
          "summary",
          "notes",
          "responseClass",
          "nickname",
          position,
          emptyList,
          emptyList,
          emptyList,
          emptyList,
          emptyList,
          emptyList,
          toOption("false")
    )
  }
}
