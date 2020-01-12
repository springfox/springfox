/*
 *
 *  Copyright 2017 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.swagger1.mixins

import org.mapstruct.factory.Mappers
import springfox.documentation.schema.mixins.ModelProviderSupport
import springfox.documentation.swagger.mixins.SwaggerPluginsSupport
import springfox.documentation.swagger1.mappers.AllowableValuesMapper
import springfox.documentation.swagger1.mappers.AuthorizationTypesMapper
import springfox.documentation.swagger1.mappers.DataTypeMapper
import springfox.documentation.swagger1.mappers.ServiceModelToSwaggerMapper

@SuppressWarnings("GrMethodMayBeStatic")
trait MapperSupport implements ModelProviderSupport, SwaggerPluginsSupport {
  DataTypeMapper dataTypeMapper() {
    new DataTypeMapper()
  }

  AuthorizationTypesMapper authMapper() {
    Mappers.getMapper(AuthorizationTypesMapper)
  }

  AllowableValuesMapper allowableValuesMapper() {
    Mappers.getMapper(AllowableValuesMapper)
  }

  ServiceModelToSwaggerMapper serviceMapper() {
    def mapper = Mappers.getMapper(ServiceModelToSwaggerMapper)
    mapper.authorizationTypesMapper = authMapper()
    mapper.allowableValuesMapper = allowableValuesMapper()
    mapper.dataTypeMapper = dataTypeMapper()
    mapper
  }
}
