package com.mangofactory.documentation.builders
import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.ModelRef
import com.mangofactory.documentation.service.AllowableListValues
import spock.lang.Specification

class ModelPropertyBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod       | value                                 | property
      'position'          | 1                                     | 'position'
      'name'              | 'model1'                              | 'name'
      'type'              | new TypeResolver().resolve(String)    | 'type'
      'qualifiedType'     | 'com.Model1'                          | 'qualifiedType'
      'description'       | 'model1 desc'                         | 'description'
      'required'          | true                                  | 'required'
      'typeName'          | 'model1'                              | 'typeName'
      'allowableValues'   | new AllowableListValues(['a'], "LIST")| 'allowableValues'
      'modelRef'          | new ModelRef("test")                  | 'modelRef'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'name'              | 'model1'                              | 'name'
      'type'              | new TypeResolver().resolve(String)    | 'type'
      'qualifiedType'     | 'com.Model1'                          | 'qualifiedType'
      'description'       | 'model1 desc'                         | 'description'
      'typeName'          | 'model1'                              | 'typeName'
      'allowableValues'   | new AllowableListValues(['a'], "LIST")| 'allowableValues'
      'modelRef'          | Mock(ModelRef)                        | 'modelRef'
  }

  def "When allowable list value is empty builder sets the value to null"() {
    given:
      def sut = new ModelPropertyBuilder()
    when:
      sut.allowableValues(new AllowableListValues([], "LIST"))
    and:
      def built = sut.build()
    then:
      built.allowableValues == null
  }
}
