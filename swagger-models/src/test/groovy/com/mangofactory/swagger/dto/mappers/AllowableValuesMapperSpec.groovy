package com.mangofactory.swagger.dto.mappers
import com.mangofactory.service.model.AllowableListValues
import com.mangofactory.service.model.AllowableRangeValues
import com.mangofactory.swagger.mixins.MapperSupport
import spock.lang.Specification

@Mixin(MapperSupport)
class AllowableValuesMapperSpec extends Specification {
  def "Maps allowable list correctly"() {
    given:
      AllowableListValues source = new AllowableListValues(['ONE', 'TWO'], "string")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.swagger.dto.AllowableListValues dto = mapper.toSwagger(source)
    then:
      dto.values.containsAll(['ONE', 'TWO'])
      dto.valueType == "string"
  }
  def "Maps allowable ranges correctly"() {
    given:
      AllowableRangeValues source = new AllowableRangeValues("0", "5")
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      com.mangofactory.swagger.dto.AllowableRangeValues dto = mapper.toSwagger(source)
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
      def rangeDto = mapper.concreteToSwagger(rangeSource)
      def listDto = mapper.concreteToSwagger(listSource)
    then:
      rangeDto instanceof com.mangofactory.swagger.dto.AllowableRangeValues
      rangeDto.min == "0"
      rangeDto.max == "5"
      listDto instanceof com.mangofactory.swagger.dto.AllowableListValues
      listDto.values.containsAll(['ONE', 'TWO'])
      listDto.valueType == "string"
  }
}
