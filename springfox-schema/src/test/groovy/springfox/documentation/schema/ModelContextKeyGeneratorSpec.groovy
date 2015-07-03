package springfox.documentation.schema

import spock.lang.Specification
import springfox.documentation.schema.mixins.TypesForTestingSupport
import springfox.documentation.spi.schema.contexts.ModelContext

@Mixin(TypesForTestingSupport)
class ModelContextKeyGeneratorSpec extends Specification {
  def "Exception when when param isnt of type ModelContext" () {
    given:
      ModelContextKeyGenerator sut = new ModelContextKeyGenerator()
    when:
      sut.generate(null, null, args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], [""], null]
  }

  def "Exception when param isnt of type ModelContext" () {
    when:
      ModelCacheKeys.modelContextKey(args)
    then:
      thrown(Exception)
    where:
      args << [[], [null], [""], null]
  }

  def "Generates key when param is of type ModelContext" () {
    given:
      ModelContextKeyGenerator sut = new ModelContextKeyGenerator()
    and:
      def context = Mock(ModelContext)
      context.isReturnType() >> true
      context.resolvedType(_) >> genericClassOfType(String)
    when:
      def key = sut.generate(null, null, context)
    then:
      key == "springfox.documentation.schema.GenericType<java.lang.String>(true)"
  }
}
