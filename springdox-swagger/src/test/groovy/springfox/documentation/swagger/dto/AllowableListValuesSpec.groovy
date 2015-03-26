package springfox.documentation.swagger.dto

class AllowableListValuesSpec extends InternalJsonSerializationSpec {

  def "should pass coverage"() {
    expect:
      new springfox.documentation.service.AllowableListValues(['a'], 'string').with {
        getValues()
        getValueType()
      }
  }

  def "when list values is empty, the enum value is ignored" () {
    expect:
      writePretty(new AllowableListValues([], "List")) == """{ }"""
  }

  def "when list values is not empty, the enum value is rendered" () {
    expect:
      writePretty(new AllowableListValues(['ONE', 'TWO'], "List")) == """{
  "enum" : [ "ONE", "TWO" ]
}"""
  }
}
