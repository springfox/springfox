package springfox.documentation.swagger.mixins

import org.mapstruct.factory.Mappers
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.swagger.mappers.AuthorizationTypesMapper
import springfox.documentation.swagger.mappers.AllowableValuesMapper
import springfox.documentation.swagger.mappers.DataTypeMapper
import springfox.documentation.swagger.mappers.ServiceModelToSwaggerMapper

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([ModelProviderSupport, SwaggerPluginsSupport])
class MapperSupport {
  DataTypeMapper dataTypeMapper() {
    new DataTypeMapper()
  }
  AuthorizationTypesMapper authMapper() {
    Mappers.getMapper(AuthorizationTypesMapper)
  }
  AllowableValuesMapper allowableValuesMapper() {
    Mappers.getMapper(AllowableValuesMapper)
  }
  //TODO: make this an integration test with spring DI and beans autowired
  ServiceModelToSwaggerMapper serviceMapper() {
    def mapper = Mappers.getMapper(ServiceModelToSwaggerMapper)
    mapper.authorizationTypesMapper = authMapper()
    mapper.allowableValuesMapper = allowableValuesMapper()
    mapper.dataTypeMapper = dataTypeMapper()
    mapper

  }

}
