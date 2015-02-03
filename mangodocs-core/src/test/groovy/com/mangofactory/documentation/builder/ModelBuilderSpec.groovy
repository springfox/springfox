package com.mangofactory.documentation.builder

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.ModelProperty
import spock.lang.Specification

class ModelBuilderSpec extends Specification {
  def "Setting properties on the builder with non-null values"() {
    given:
      def sut = new ModelBuilder()
    when:
      sut."$builderMethod"(value)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'id'            | 'model1'                              | 'id'
      'name'          | 'model1'                              | 'name'
      'qualifiedType' | 'com.Model1'                          | 'qualifiedType'
      'description'   | 'model1 desc'                         | 'description'
      'baseModel'     | 'baseModel1'                          | 'baseModel'
      'discriminator' | 'decriminator'                        | 'discriminator'
      'type'          | new TypeResolver().resolve(String)    | 'type'
      'subTypes'      | ["String"]                            | 'subTypes'
      'properties'    | [p1: Mock(ModelProperty)]             | 'properties'
  }

  def "Setting builder properties to null values preserves existing values"() {
    given:
      def sut = new ModelBuilder()
    when:
      sut."$builderMethod"(value)
      sut."$builderMethod"(null)
    and:
      def built = sut.build()
    then:
      built."$property" == value

    where:
      builderMethod   | value                                 | property
      'id'            | 'model1'                              | 'id'
      'name'          | 'model1'                              | 'name'
      'qualifiedType' | 'com.Model1'                          | 'qualifiedType'
      'description'   | 'model1 desc'                         | 'description'
      'baseModel'     | 'baseModel1'                          | 'baseModel'
      'discriminator' | 'decriminator'                        | 'discriminator'
      'type'          | new TypeResolver().resolve(String)    | 'type'
      'subTypes'      | ["String"]                            | 'subTypes'
      'properties'    | [p1: Mock(ModelProperty)]             | 'properties'
  }
}
