package com.mangofactory.swagger.mixins

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.schema.DefaultGenericTypeNamingStrategy
import com.mangofactory.schema.TypeNameExtractor
import com.mangofactory.swagger.dto.mappers.AllowableValuesMapper
import com.mangofactory.swagger.dto.mappers.AllowableValuesMapperImpl
import com.mangofactory.swagger.dto.mappers.AuthorizationTypesMapper
import com.mangofactory.swagger.dto.mappers.AuthorizationTypesMapperImpl
import com.mangofactory.swagger.dto.mappers.DataTypeMapper
import com.mangofactory.swagger.dto.mappers.ServiceModelToSwaggerMapper
import com.mangofactory.swagger.dto.mappers.ServiceModelToSwaggerMapperImpl

@SuppressWarnings("GrMethodMayBeStatic")
@Mixin(ModelProviderSupport)
class MapperSupport {
  DataTypeMapper dataTypeMapper() {
    new DataTypeMapper(new TypeNameExtractor(new TypeResolver(), new DefaultGenericTypeNamingStrategy(),
            pluginsManager()))
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
