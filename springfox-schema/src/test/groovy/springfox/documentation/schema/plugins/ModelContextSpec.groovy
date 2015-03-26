package springfox.documentation.schema.plugins

import spock.lang.Shared
import spock.lang.Specification
import springfox.documentation.schema.DefaultGenericTypeNamingStrategy
import springfox.documentation.schema.ExampleEnum
import springfox.documentation.schema.ExampleWithEnums
import springfox.documentation.spi.schema.AlternateTypeProvider
import springfox.documentation.spi.schema.contexts.ModelContext

import static springfox.documentation.spi.DocumentationType.*
import static springfox.documentation.spi.schema.contexts.ModelContext.*

class ModelContextSpec extends Specification {
  @Shared
  AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  @Shared
  def namingStrategy = new DefaultGenericTypeNamingStrategy()

  def "ModelContext equals works as expected"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                                                               | expectedEquality
      inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)      | true
      inputParam(ExampleWithEnums, SWAGGER_12, provider, namingStrategy) | false
      returnValue(ExampleEnum, SWAGGER_12, provider, namingStrategy)     | false
      ExampleEnum                                                        | false
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)
      ModelContext other = inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)
      ModelContext otherReturn = returnValue(ExampleEnum, SWAGGER_12, provider, namingStrategy)
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
