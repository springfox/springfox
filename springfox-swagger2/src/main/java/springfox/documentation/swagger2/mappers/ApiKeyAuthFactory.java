/*
 *
 *  Copyright 2015-2019 the original author or authors.
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

package springfox.documentation.swagger2.mappers;

import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.models.auth.In;
import io.swagger.models.auth.SecuritySchemeDefinition;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.SecurityScheme;

class ApiKeyAuthFactory implements SecuritySchemeFactory {
  @Override
  public SecuritySchemeDefinition create(SecurityScheme input) {
    ApiKey apiKey = (ApiKey) input;
    VendorExtensionsMapper vendorMapper = new VendorExtensionsMapper();
    ApiKeyAuthDefinition definition = new ApiKeyAuthDefinition();
    definition.name(apiKey.getKeyname()).in(In.forValue(apiKey.getPassAs()));
    definition.setVendorExtensions(vendorMapper.mapExtensions(apiKey.getVendorExtensions()));
    return definition;
  }
}
