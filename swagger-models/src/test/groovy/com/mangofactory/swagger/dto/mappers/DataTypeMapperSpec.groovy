package com.mangofactory.swagger.dto.mappers
import com.mangofactory.service.model.ContainerDataType
import com.mangofactory.service.model.DataType
import com.mangofactory.service.model.ModelRef
import com.mangofactory.service.model.ReferenceDataType
import com.mangofactory.service.model.SwaggerDataType
import com.mangofactory.swagger.mixins.MapperSupport
import spock.lang.Specification

@Mixin(MapperSupport)
class DataTypeMapperSpec extends Specification {
  def "Maps primitive data type"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new DataType(type).getDataType()
      def mapped = sut.toSwaggerPrimitiveDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.type == built.type
      mapped.absoluteType == built.absoluteType
      abstractMapped.type == built.type
      abstractMapped.absoluteType == built.absoluteType
    where:
      type << ["void", "string", "boolean", "BigDecimal", "BigInteger"]
  }

  def "Maps primitive data type with format"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new DataType(initialType).getDataType()
      def mapped = sut.toSwaggerPrimitiveFormatDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.type == built.type
      mapped.format == built.format
      mapped.absoluteType == built.absoluteType
      abstractMapped.type == built.type
      abstractMapped.format == built.format
      abstractMapped.absoluteType == built.absoluteType
    where:
      initialType | type        | format
      "int"       | "integer"   | "int32"
      "long"      | "integer"   | "int64"
      "float"     | "number"    | "float"
      "double"    | "number"    | "double"
      "byte"      | "string"    | "byte"
      "Date"      | "string"    | "date-time"
      "DateTime"  | "string"    | "date-time"
      "date-time" | "string"    | "date-time"
      "UUID"      | "string"    | "uuid"
      "date"      | "string"    | "date"


  }

  def "Maps model reference"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ModelRef("RefType")
      def mapped = sut.toSwaggerModelRef(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.type.absoluteType == built.type.absoluteType
      mapped.absoluteType == built.absoluteType
      abstractMapped.type.absoluteType == built.type.absoluteType
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps reference data type"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ReferenceDataType("RefType")
      def mapped = sut.toSwaggerReferenceDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.reference == built.reference
      mapped.absoluteType == built.absoluteType
      abstractMapped.reference == built.reference
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps container type with primitive"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ContainerDataType("string", true)
      def mapped = sut.toSwaggerContainerDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.items.dataType.type == built.items.dataType.type
      mapped.uniqueItems == built.uniqueItems
      mapped.absoluteType == built.absoluteType
      abstractMapped.items.dataType.type == built.items.dataType.type
      abstractMapped.uniqueItems == built.uniqueItems
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps container type with Reference"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ContainerDataType("ReferenceType", true)
      def mapped = sut.toSwaggerContainerDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.items.dataType.reference == built.items.dataType.reference
      mapped.uniqueItems == built.uniqueItems
      mapped.absoluteType == built.absoluteType
      abstractMapped.items.dataType.reference == built.items.dataType.reference
      abstractMapped.uniqueItems == built.uniqueItems
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps container type with primitive format"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ContainerDataType("int", true)
      def mapped = sut.toSwaggerContainerDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.items.dataType.type == built.items.dataType.type
      mapped.items.dataType.format == built.items.dataType.format
      mapped.uniqueItems == built.uniqueItems
      mapped.absoluteType == built.absoluteType
      abstractMapped.items.dataType.type == built.items.dataType.type
      abstractMapped.items.dataType.format == built.items.dataType.format
      abstractMapped.uniqueItems == built.uniqueItems
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps unique container type with primitive format"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new ContainerDataType("int", false)
      def mapped = sut.toSwaggerContainerDataType(built)
    and:
      def abstractMapped = sut.toSwaggerSwaggerDataType(built)
    then:
      mapped.items.dataType.type == built.items.dataType.type
      mapped.items.dataType.format == built.items.dataType.format
      mapped.uniqueItems == built.uniqueItems
      mapped.absoluteType == built.absoluteType
      abstractMapped.items.dataType.type == built.items.dataType.type
      abstractMapped.items.dataType.format == built.items.dataType.format
      abstractMapped.uniqueItems == built.uniqueItems
      abstractMapped.absoluteType == built.absoluteType
  }

  def "Maps container data type"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new DataType("array[int]")
      def mapped = sut.toSwaggerDataType(built)
    then:
      mapped.dataType.absoluteType == built.dataType.absoluteType
      mapped.dataType.type == built.dataType.type
      mapped.dataType.uniqueItems == built.dataType.uniqueItems
      mapped.dataType.items.dataType.type == built.dataType.items.dataType.type
      mapped.dataType.items.dataType.format == built.dataType.items.dataType.format
  }

  def "Maps set container data type"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new DataType("Set[int]")
      def mapped = sut.toSwaggerDataType(built)
    then:
      mapped.dataType.absoluteType == built.dataType.absoluteType
      mapped.dataType.type == built.dataType.type
      mapped.dataType.uniqueItems == built.dataType.uniqueItems
      mapped.dataType.items.dataType.type == built.dataType.items.dataType.type
      mapped.dataType.items.dataType.format == built.dataType.items.dataType.format
  }

  def "Maps data type"() {
    given:
      def sut = dataTypeMapper()
    when:
      def built = new DataType("ReferenceType")
      def mapped = sut.toSwaggerDataType(built)
    then:
      mapped.dataType.absoluteType == built.dataType.absoluteType
      mapped.dataType.reference == built.dataType.reference
  }

  def "Throws an exception when it encounters an unknown type"() {
    given:
      def sut = dataTypeMapper()
    when:
      sut.toSwaggerSwaggerDataType(new SwaggerDataType() {
        @Override
        String getAbsoluteType() {
          return "unknown"
        }
      })
    then:
     thrown(UnsupportedOperationException)
  }
}
