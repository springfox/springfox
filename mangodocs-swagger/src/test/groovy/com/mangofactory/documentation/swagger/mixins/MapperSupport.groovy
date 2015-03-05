package com.mangofactory.documentation.swagger.mixins
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport
import com.mangofactory.documentation.swagger.mappers.AllowableValuesMapper
import com.mangofactory.documentation.swagger.mappers.AuthorizationTypesMapper
import com.mangofactory.documentation.swagger.mappers.DataTypeMapper
import com.mangofactory.documentation.swagger.mappers.ServiceModelToSwaggerMapper
import org.mapstruct.factory.Mappers

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
