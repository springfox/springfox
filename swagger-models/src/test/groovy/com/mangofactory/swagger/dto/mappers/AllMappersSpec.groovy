package com.mangofactory.swagger.dto.mappers
import com.mangofactory.service.model.AllowableListValues
import com.mangofactory.service.model.AllowableRangeValues
import com.mangofactory.service.model.ApiInfo
import com.mangofactory.service.model.ApiKey
import com.mangofactory.service.model.ApiListingReference
import com.mangofactory.service.model.Authorization
import com.mangofactory.service.model.AuthorizationCodeGrant
import com.mangofactory.service.model.AuthorizationScope
import com.mangofactory.service.model.BasicAuth
import com.mangofactory.service.model.ContainerDataType
import com.mangofactory.service.model.ImplicitGrant
import com.mangofactory.service.model.LoginEndpoint
import com.mangofactory.service.model.ModelRef
import com.mangofactory.service.model.OAuth
import com.mangofactory.service.model.PrimitiveDataType
import com.mangofactory.service.model.PrimitiveFormatDataType
import com.mangofactory.service.model.ReferenceDataType
import com.mangofactory.service.model.ResourceListing
import com.mangofactory.service.model.TokenEndpoint
import com.mangofactory.service.model.TokenRequestEndpoint
import com.mangofactory.swagger.dto.ApiDescription
import com.mangofactory.swagger.dto.ApiListing
import com.mangofactory.swagger.dto.ModelDto
import com.mangofactory.swagger.dto.ModelPropertyDto
import com.mangofactory.swagger.dto.Operation
import com.mangofactory.swagger.dto.Parameter
import com.mangofactory.swagger.dto.ResponseMessage
import com.mangofactory.swagger.mixins.MapperSupport
import spock.lang.Specification

@Mixin(MapperSupport)
public class AllMappersSpec extends Specification {
  def "All AuthorizationTypesMapper null sources are mapped to null targets"() {
    given:
      AuthorizationTypesMapper mapper = authMapper()
    when:
      def mapped = mapper."toSwagger$typeToTest.simpleName"(null)
    then:
      mapped == null
    where:
      typeToTest << [ApiKey, OAuth, BasicAuth, ImplicitGrant, AuthorizationCodeGrant, TokenEndpoint,
                     TokenRequestEndpoint, AuthorizationScope, Authorization, LoginEndpoint]
  }

  def "All AllowableValuesMapper null sources are mapped to null targets"() {
    given:
      AllowableValuesMapper mapper = allowableValuesMapper()
    when:
      def mapped = mapper."toSwagger$typeToTest.simpleName"(null)
    then:
      mapped == null
    where:
      typeToTest << [AllowableListValues, AllowableRangeValues]
  }

  def "All DataTypeMapper null sources are mapped to null targets"() {
    given:
      DataTypeMapper mapper = dataTypeMapper()
    when:
      def mapped = mapper."toSwagger$typeToTest.simpleName"(null)
    then:
      mapped == null
    where:
      typeToTest << [PrimitiveDataType, PrimitiveFormatDataType, ModelRef, ReferenceDataType, ContainerDataType]
  }

  def "All ServiceModelToSwaggerMapper null sources are mapped to null targets"() {
    given:
      ServiceModelToSwaggerMapper mapper = serviceMapper()
    when:
      def mapped = mapper."toSwagger$typeToTest.simpleName"(null)
    then:
      mapped == null
    where:
      typeToTest << [ApiDescription, ApiInfo, ApiListing, ApiListingReference, ModelDto, ModelPropertyDto, Operation,
                     Parameter, ResourceListing, ResponseMessage]
  }
}
