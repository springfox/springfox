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

import io.swagger.oas.models.OpenAPI;
import io.swagger.oas.models.info.Contact;
import io.swagger.oas.models.info.Info;
import io.swagger.oas.models.tags.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Documentation;

@Mapper(uses = { VendorExtensionsMapper.class, LicenseMapper.class })
public abstract class ServiceModelToOasMapper {
  @Mappings({
      @Mapping(target = "openapi", constant = "3.0.0"),
      @Mapping(target = "info", source = "resourceListing.info"),
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "servers", ignore = true),
      @Mapping(target = "security", ignore = true),
      @Mapping(target = "paths", ignore = true),
      @Mapping(target = "components", ignore = true),
      @Mapping(target = "extensions", source = "vendorExtensions")
  })
  public abstract OpenAPI mapDocumentation(Documentation from);

  @Mappings({
      @Mapping(target = "license", source = "from",
          qualifiedBy = { LicenseMapper.LicenseTranslator.class, LicenseMapper.License.class }),
      @Mapping(target = "contact", source = "from.contact"),
      @Mapping(target = "termsOfService", source = "termsOfServiceUrl"),
      @Mapping(target = "extensions", source = "vendorExtensions")
  })
  protected abstract Info mapApiInfo(ApiInfo from);

  @Mappings({
      @Mapping(target = "extensions", ignore = true)
  })
  protected abstract Contact map(springfox.documentation.service.Contact from);

  @Mappings({
      @Mapping(target = "externalDocs", ignore = true),
      @Mapping(target = "extensions", ignore = true)
  })
  protected abstract Tag mapTag(springfox.documentation.service.Tag from);
}
