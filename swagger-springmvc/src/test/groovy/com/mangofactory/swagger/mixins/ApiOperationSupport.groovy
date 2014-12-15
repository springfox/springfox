package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.models.dto.Operation


class ApiOperationSupport {

  def operation(int position = 0, String method = "someMethod") {
    def emptyList = []
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
            [] as Set,
            "false"
    )
  }
}
