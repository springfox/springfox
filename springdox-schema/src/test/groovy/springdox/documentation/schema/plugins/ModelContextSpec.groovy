package springdox.documentation.schema.plugins
import spock.lang.Shared
import spock.lang.Specification
import springdox.documentation.schema.DefaultGenericTypeNamingStrategy
import springdox.documentation.schema.ExampleEnum
import springdox.documentation.schema.ExampleWithEnums
import springdox.documentation.spi.schema.AlternateTypeProvider
import springdox.documentation.spi.schema.contexts.ModelContext

import static springdox.documentation.spi.DocumentationType.*
import static springdox.documentation.spi.schema.contexts.ModelContext.*

class ModelContextSpec extends Specification {
  @Shared AlternateTypeProvider provider = Mock(AlternateTypeProvider)
  @Shared def namingStrategy = new DefaultGenericTypeNamingStrategy()
  def "ModelContext equals works as expected"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)
    expect:
      context.equals(test) == expectedEquality
      context.equals(context)
    where:
      test                                                                | expectedEquality
      inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)       | true
      inputParam(ExampleWithEnums, SWAGGER_12, provider, namingStrategy)  | false
      returnValue(ExampleEnum, SWAGGER_12, provider, namingStrategy)      | false
      ExampleEnum                                                         | false
  }

  def "ModelContext hashcode generated takes into account immutable values"() {
    given:
      ModelContext context = inputParam(ExampleEnum, SWAGGER_12,  provider, namingStrategy)
      ModelContext other = inputParam(ExampleEnum, SWAGGER_12, provider, namingStrategy)
      ModelContext otherReturn = returnValue(ExampleEnum, SWAGGER_12, provider, namingStrategy)
    expect:
      context.hashCode() == other.hashCode()
      context.hashCode() != otherReturn.hashCode()
  }
}
