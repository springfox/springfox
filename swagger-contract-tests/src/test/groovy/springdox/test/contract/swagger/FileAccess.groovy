package springdox.test.contract.swagger

trait FileAccess {
  String fileContents(String fileName) {
    this.getClass().getResource("$fileName").text
  }
}