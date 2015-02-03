package com.mangofactory.documentation.swagger.dto.mappers
import com.mangofactory.documentation.service.AllowableListValues
import com.mangofactory.documentation.service.AllowableRangeValues
import com.mangofactory.documentation.service.ApiInfo
import com.mangofactory.documentation.service.ApiKey
import com.mangofactory.documentation.service.ApiListingReference
import com.mangofactory.documentation.service.Authorization
import com.mangofactory.documentation.service.AuthorizationCodeGrant
import com.mangofactory.documentation.service.AuthorizationScope
import com.mangofactory.documentation.service.BasicAuth
import com.mangofactory.documentation.service.ImplicitGrant
import com.mangofactory.documentation.service.LoginEndpoint
import com.mangofactory.documentation.service.OAuth
import com.mangofactory.documentation.service.ResourceListing
import com.mangofactory.documentation.service.TokenEndpoint
import com.mangofactory.documentation.service.TokenRequestEndpoint
import com.mangofactory.documentation.swagger.dto.ApiDescription
import com.mangofactory.documentation.swagger.dto.ApiListing
import com.mangofactory.documentation.swagger.dto.ModelDto
import com.mangofactory.documentation.swagger.dto.ModelPropertyDto
import com.mangofactory.documentation.swagger.dto.Operation
import com.mangofactory.documentation.swagger.dto.Parameter
import com.mangofactory.documentation.swagger.dto.ResponseMessage
import com.mangofactory.documentation.swagger.mixins.MapperSupport
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
