package springdox.documentation.builders

import com.fasterxml.classmate.ResolvedType
import com.google.common.base.Optional
import spock.lang.Specification
import spock.lang.Unroll
import springdox.documentation.schema.ModelRef
import springdox.documentation.service.AllowableListValues

class ParameterBulderSpec extends Specification {
  @Unroll
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      if (built."$property" instanceof Optional) {
        assert built."$property" == Optional.of(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod     | value                                 | property
      'name'            | 'param1'                              | 'name'
      'description'     | 'param1 desc'                         | 'description'
      'defaultValue'    | 'default'                             | 'defaultValue'
      'modelRef'        | new ModelRef('string')                | 'modelRef'
      'parameterType'   | 'string'                              | 'paramType'
      'parameterAccess' | 'public'                              | 'paramAccess'
      'allowMultiple'   | true                                  | 'allowMultiple'
      'required'        | true                                  | 'required'
      'allowableValues' | new AllowableListValues([], "LIST")   | 'allowableValues'
      'type'            | Mock(ResolvedType)                    | 'type'
  }

  @Unroll
  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ParameterBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      if (built."$property" instanceof Optional) {
        assert built."$property" == Optional.of(value)
      } else {
        assert built."$property" == value
      }

    where:
      builderMethod     | value                                 | property
      'name'            | 'param1'                              | 'name'
      'description'     | 'param1 desc'                         | 'description'
      'defaultValue'    | 'default'                             | 'defaultValue'
      'modelRef'        | new ModelRef('string')                | 'modelRef'
      'parameterType'   | 'string'                              | 'paramType'
      'parameterAccess' | 'public'                              | 'paramAccess'
      'allowableValues' | new AllowableListValues([], "LIST")   | 'allowableValues'
      'type'            | Mock(ResolvedType)                    | 'type'
  }
}
