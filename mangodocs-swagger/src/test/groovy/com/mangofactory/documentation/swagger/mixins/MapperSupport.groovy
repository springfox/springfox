package com.mangofactory.documentation.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.documentation.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.documentation.schema.TypeNameExtractor
import com.mangofactory.documentation.swagger.mappers.AllowableValuesMapper
import com.mangofactory.documentation.swagger.mappers.AllowableValuesMapperImpl
import com.mangofactory.documentation.swagger.mappers.AuthorizationTypesMapper
import com.mangofactory.documentation.swagger.mappers.AuthorizationTypesMapperImpl
import com.mangofactory.documentation.swagger.mappers.DataTypeMapper
import com.mangofactory.documentation.swagger.mappers.ServiceModelToSwaggerMapper
import com.mangofactory.documentation.swagger.mappers.ServiceModelToSwaggerMapperImpl
import com.mangofactory.documentation.schema.mixins.ModelProviderSupport

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin([ModelProviderSupport, SwaggerPluginsSupport])
class MapperSupport {
  DataTypeMapper dataTypeMapper() {
    new DataTypeMapper(new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(),
            swaggerSchemaPlugins()))
  }
  AuthorizationTypesMapper authMapper() {
    new AuthorizationTypesMapperImpl()
  }
  AllowableValuesMapper allowableValuesMapper() {
    new AllowableValuesMapperImpl()
  }
  ServiceModelToSwaggerMapper serviceMapper() {
    def mapper = new ServiceModelToSwaggerMapperImpl()
    mapper.authorizationTypesMapper = authMapper()
    mapper.allowableValuesMapper = allowableValuesMapper()
    mapper.dataTypeMapper = dataTypeMapper()
    mapper
  }

}
