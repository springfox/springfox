package springdox.documentation.swagger.dto

class ApiDescriptionSpec extends InternalJsonSerializationSpec {
  final ApiDescription description = new ApiDescription('p', 'd', [], true)

  def "should serialize"() {
    expect:
      writePretty(description) == """{
  "description" : "d",
  "operations" : [ ],
  "path" : "p"
}"""
  }

  def "should pass coverage"() {
    expect:
      description.getDescription()
      description.getOperations() == []
      description.getPath()
      description.isHidden()
  }
}
