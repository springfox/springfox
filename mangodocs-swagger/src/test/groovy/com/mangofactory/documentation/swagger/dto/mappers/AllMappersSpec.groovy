package com.mangofactory.documentation.swagger.dto.mappers
import com.mangofactory.documentation.service.model.AllowableListValues
import com.mangofactory.documentation.service.model.AllowableRangeValues
import com.mangofactory.documentation.service.model.ApiInfo
import com.mangofactory.documentation.service.model.ApiKey
import com.mangofactory.documentation.service.model.ApiListingReference
import com.mangofactory.documentation.service.model.Authorization
import com.mangofactory.documentation.service.model.AuthorizationCodeGrant
import com.mangofactory.documentation.service.model.AuthorizationScope
import com.mangofactory.documentation.service.model.BasicAuth
import com.mangofactory.documentation.service.model.ImplicitGrant
import com.mangofactory.documentation.service.model.LoginEndpoint
import com.mangofactory.documentation.service.model.OAuth
import com.mangofactory.documentation.service.model.ResourceListing
import com.mangofactory.documentation.service.model.TokenEndpoint
import com.mangofactory.documentation.service.model.TokenRequestEndpoint
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
