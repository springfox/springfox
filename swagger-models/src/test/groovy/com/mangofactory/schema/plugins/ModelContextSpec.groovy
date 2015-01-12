package com.mangofactory.schema.plugins
import com.mangofactory.schema.ExampleEnum
import com.mangofactory.schema.ExampleWithEnums
import spock.lang.Specification

import static com.mangofactory.schema.plugins.DocumentationType.*
import static com.mangofactory.schema.plugins.ModelContext.*

class ModelContextSpec extends Specification {
  def "ModelContext equals works as expected" () {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12)
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                               | expectedEquality
      inputParam(ExampleEnum, SWAGGER_12)       | true
      inputParam(ExampleWithEnums, SWAGGER_12)  | false
      returnValue(ExampleEnum, SWAGGER_12)      | false
      ExampleEnum                               | false
  }

  def "ModelContext hashcode generated takes into account immutable values" () {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12)
      ModelContext other = inputParam(ExampleEnum, SWAGGER_12)
      ModelContext otherReturn = returnValue(ExampleEnum, SWAGGER_12)
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
