package springdox.documentation.swagger.mappers

import spock.lang.Specification
import springdox.documentation.service.AllowableListValues
import springdox.documentation.service.AllowableRangeValues
import springdox.documentation.service.ApiInfo
import springdox.documentation.service.ApiKey
import springdox.documentation.service.ApiListingReference
import springdox.documentation.service.Authorization
import springdox.documentation.service.AuthorizationCodeGrant
import springdox.documentation.service.AuthorizationScope
import springdox.documentation.service.BasicAuth
import springdox.documentation.service.ImplicitGrant
import springdox.documentation.service.LoginEndpoint
import springdox.documentation.service.OAuth
import springdox.documentation.service.ResourceListing
import springdox.documentation.service.TokenEndpoint
import springdox.documentation.service.TokenRequestEndpoint
import springdox.documentation.swagger.dto.ApiDescription
import springdox.documentation.swagger.dto.ApiListing
import springdox.documentation.swagger.dto.ModelDto
import springdox.documentation.swagger.dto.ModelPropertyDto
import springdox.documentation.swagger.dto.Operation
import springdox.documentation.swagger.dto.Parameter
import springdox.documentation.swagger.dto.ResponseMessage
import springdox.documentation.swagger.mixins.MapperSupport

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
