package springfox.documentation.builders

import spock.lang.Specification
import spock.lang.Unroll


class AlternateTypePropertyBuilderSpec extends Specification {
  @Unroll
  def "Builds bean class with field with getter #canRead and setter #canWrite"() {
    given:
    def sut = new AlternateTypeBuilder()

    when:
    def built = sut
        .fullyQualifiedClassName(className)
        .property {
          it.name(propertyName)
              .type(String)
              .canRead(canRead)
              .canWrite(canWrite)
        }
        .annotations([])
        .build()

    then:
    built.declaredFields.find { it.name == propertyName } != null
    hasMethod(built, "get$propertyName") == canRead
    hasMethod(built, "set$propertyName") == canWrite

    where:
    className      | propertyName | canRead | canWrite
    "a.b.NewClass" | "test"       | false   | false
    "a.b.NewClass" | "test"       | true    | false
    "a.b.NewClass" | "test"       | false   | true
    "a.b.NewClass" | "test"       | true    | true
  }

  def hasMethod(Class<?> built, methodName) {
    built.declaredMethods.find { "${it.name}".toLowerCase() == methodName } != null
  }
}
