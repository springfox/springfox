package springdox.documentation.schema.plugins

import spock.lang.Shared
import spock.lang.Specification
import springdox.documentation.schema.ExampleEnum
import springdox.documentation.schema.ExampleWithEnums
import springdox.documentation.spi.schema.AlternateTypeProvider
import springdox.documentation.spi.schema.contexts.ModelContext

import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

class ModelContextSpec extends Specification {
  @Shared AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  def "ModelContext equals works as expected"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12, provider)
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                                                | expectedEquality
      inputParam(ExampleEnum, SWAGGER_12, provider)       | true
      inputParam(ExampleWithEnums, SWAGGER_12, provider)  | false
      returnValue(ExampleEnum, SWAGGER_12, provider)      | false
      ExampleEnum                                         | false
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12,  provider)
      ModelContext other = inputParam(ExampleEnum, SWAGGER_12, provider)
      ModelContext otherReturn = returnValue(ExampleEnum, SWAGGER_12, provider)
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
