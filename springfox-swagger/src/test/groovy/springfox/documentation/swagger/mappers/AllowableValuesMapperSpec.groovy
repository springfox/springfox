package springfox.documentation.swagger.mappers

import spock.lang.Specification
import springfox.documentation.service.AllowableValues
import springfox.documentation.swagger.dto.AllowableListValues
import springfox.documentation.swagger.dto.AllowableRangeValues
import springfox.documentation.swagger.mixins.MapperSupport

@Mixin(MapperSupport)
class AllowableValuesMapperSpec extends Specification {
  def "Maps null range input to null output"() {
    given:
      springfox.documentation.service.AllowableRangeValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto == source
  }

  def "Maps null list values input to null output"() {
    given:
      springfox.documentation.service.AllowableListValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto == source
  }

  def "Maps list values input when values is null"() {
    given:
      springfox.documentation.service.AllowableListValues source = new springfox.documentation.service.AllowableListValues(null, "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values == null
      dto.valueType == "string"
  }

  def "Maps allowable list correctly"() {
    given:
      springfox.documentation.service.AllowableListValues source = new springfox.documentation.service.AllowableListValues(['ONE', 'TWO'], "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values.containsAll(['ONE', 'TWO'])
      dto.valueType == "string"
  }

  def "Maps allowable ranges correctly"() {
    given:
      springfox.documentation.service.AllowableRangeValues source = new springfox.documentation.service.AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto.min == "0"
      dto.max == "5"
  }

  def "Maps abstract values correctly correctly"() {
    given:
      springfox.documentation.service.AllowableListValues listSource = new springfox.documentation.service.AllowableListValues(['ONE', 'TWO'], "string")
      springfox.documentation.service.AllowableRangeValues rangeSource = new springfox.documentation.service.AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      def rangeDto = mapper.toSwaggerAllowableValues(rangeSource)
      def listDto = mapper.toSwaggerAllowableValues(listSource)
      def nullDto = mapper.toSwaggerAllowableValues(null)
    then:
      rangeDto instanceof AllowableRangeValues
      rangeDto.min == "0"
      rangeDto.max == "5"
      listDto instanceof AllowableListValues
      listDto.values.containsAll(['ONE', 'TWO'])
      listDto.valueType == "string"
      nullDto == null
  }

  def "AllowableValuesMapper handles unmapped values type"() {
    given:
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      mapper.toSwaggerAllowableValues(new AllowableValues() {})
    then:
      thrown(UnsupportedOperationException)
  }
}
