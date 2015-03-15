package springdox.documentation.swagger.mixins

import org.mapstruct.factory.Mappers
import springdox.documentation.schema.mixins.ModelProviderSupport
import springdox.documentation.swagger.mappers.AllowableValuesMapper
import springdox.documentation.swagger.mappers.AuthorizationTypesMapper
import springdox.documentation.swagger.mappers.DataTypeMapper
import springdox.documentation.swagger.mappers.ServiceModelToSwaggerMapper

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
