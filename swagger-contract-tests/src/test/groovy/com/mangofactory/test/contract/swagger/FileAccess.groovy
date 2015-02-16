package com.mangofactory.test.contract.swagger

trait FileAccess {
  String fileContents(String fileName) {
    this.getClass().getResource("/contract/swagger/$fileName").text
  }
}