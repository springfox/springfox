package com.mangofactory.documentation.swagger.mappers
import com.mangofactory.documentation.service.AllowableListValues
import com.mangofactory.documentation.service.AllowableRangeValues
import com.mangofactory.documentation.service.AllowableValues
import com.mangofactory.documentation.swagger.mixins.MapperSupport
import spock.lang.Specification

@Mixin(MapperSupport)
class AllowableValuesMapperSpec extends Specification {
  def "Maps null range input to null output"() {
    given:
      AllowableRangeValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.documentation.swagger.dto.AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto == source
  }

  def "Maps null list values input to null output"() {
    given:
      AllowableListValues source = null
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.documentation.swagger.dto.AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto == source
  }

  def "Maps list values input when values is null"() {
    given:
      AllowableListValues source = new AllowableListValues(null, "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.documentation.swagger.dto.AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values == null
      dto.valueType == "string"
  }

  def "Maps allowable list correctly"() {
    given:
      AllowableListValues source = new AllowableListValues(['ONE', 'TWO'], "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.documentation.swagger.dto.AllowableListValues dto = mapper.toSwaggerAllowableListValues(source)
    then:
      dto.values.containsAll(['ONE', 'TWO'])
      dto.valueType == "string"
  }

  def "Maps allowable ranges correctly"() {
    given:
      AllowableRangeValues source = new AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.documentation.swagger.dto.AllowableRangeValues dto = mapper.toSwaggerAllowableRangeValues(source)
    then:
      dto.min == "0"
      dto.max == "5"
  }

  def "Maps abstract values correctly correctly"() {
    given:
      AllowableListValues listSource = new AllowableListValues(['ONE', 'TWO'], "string")
      AllowableRangeValues rangeSource = new AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      def rangeDto = mapper.toSwaggerAllowableValues(rangeSource)
      def listDto = mapper.toSwaggerAllowableValues(listSource)
      def nullDto = mapper.toSwaggerAllowableValues(null)
    then:
      rangeDto instanceof com.mangofactory.documentation.swagger.dto.AllowableRangeValues
      rangeDto.min == "0"
      rangeDto.max == "5"
      listDto instanceof com.mangofactory.documentation.swagger.dto.AllowableListValues
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
