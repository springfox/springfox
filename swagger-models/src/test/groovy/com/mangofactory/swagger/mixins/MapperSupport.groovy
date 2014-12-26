package com.mangofactory.swagger.mixins

import com.mangofactory.swagger.dto.mappers.AllowableValuesMapper
import com.mangofactory.swagger.dto.mappers.AllowableValuesMapperImpl
import com.mangofactory.swagger.dto.mappers.AuthorizationTypesMapper
import com.mangofactory.swagger.dto.mappers.AuthorizationTypesMapperImpl
import com.mangofactory.swagger.dto.mappers.DataTypeMapper
import com.mangofactory.swagger.dto.mappers.DataTypeMapperImpl
import com.mangofactory.swagger.dto.mappers.ServiceModelToSwaggerMapper
import com.mangofactory.swagger.dto.mappers.ServiceModelToSwaggerMapperImpl

@SuppressWarnings("GrMethodMayBeStatic")
class MapperSupport {
  DataTypeMapper dataTypeMapper() {
    new DataTypeMapperImpl()
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
