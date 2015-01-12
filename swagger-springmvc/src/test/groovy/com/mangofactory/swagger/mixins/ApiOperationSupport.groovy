package com.mangofactory.swagger.mixins

import com.mangofactory.service.model.Operation


class ApiOperationSupport {

  def operation(int position = 0, String method = "someMethod") {
    def emptyList = []
    def emptySet = [] as Set
    new Operation(
            method,
            "summary",
            "notes",
            "responseClass",
            "nickname",
            position,
            emptySet,
            emptySet,
            emptySet,
            emptyList,
            emptyList,
            emptySet,
            "false",
            false)
  }
}
