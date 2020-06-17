/*
 *
 *  Copyright 2017-2018 the original author or authors.
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
package springfox.documentation.oas.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Qualifier;
import springfox.documentation.service.ApiInfo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapper(componentModel = "spring", implementationName = "OasLicenceMapper")
public class LicenseMapper {

  @License
  public io.swagger.v3.oas.models.info.License apiInfoToLicense(ApiInfo from) {
    if (from.getLicense() == null && from.getLicenseUrl() == null) {
      return null;
    }
    return new io.swagger.v3.oas.models.info.License().name(from.getLicense()).url(from.getLicenseUrl());
  }


  @Qualifier
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.SOURCE)
  @interface LicenseTranslator {
  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface License {
  }
}