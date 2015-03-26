/*
 *
 *  Copyright 2015 the original author or authors.
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

package springfox.documentation.swagger.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import springfox.documentation.swagger.dto.AllowableListValues;
import springfox.documentation.swagger.dto.AllowableRangeValues;
import springfox.documentation.swagger.dto.ApiDescription;
import springfox.documentation.swagger.dto.ApiInfo;
import springfox.documentation.swagger.dto.ApiKey;
import springfox.documentation.swagger.dto.ApiListing;
import springfox.documentation.swagger.dto.ApiListingReference;
import springfox.documentation.swagger.dto.Authorization;
import springfox.documentation.swagger.dto.AuthorizationCodeGrant;
import springfox.documentation.swagger.dto.AuthorizationScope;
import springfox.documentation.swagger.dto.BasicAuth;
import springfox.documentation.swagger.dto.ContainerDataType;
import springfox.documentation.swagger.dto.DataType;
import springfox.documentation.swagger.dto.ImplicitGrant;
import springfox.documentation.swagger.dto.LoginEndpoint;
import springfox.documentation.swagger.dto.ModelDto;
import springfox.documentation.swagger.dto.ModelPropertyDto;
import springfox.documentation.swagger.dto.OAuth;
import springfox.documentation.swagger.dto.Parameter;
import springfox.documentation.swagger.dto.PrimitiveDataType;
import springfox.documentation.swagger.dto.PrimitiveFormatDataType;
import springfox.documentation.swagger.dto.ReferenceDataType;
import springfox.documentation.swagger.dto.ResourceListing;
import springfox.documentation.swagger.dto.ResponseMessage;
import springfox.documentation.swagger.dto.TokenEndpoint;
import springfox.documentation.swagger.dto.TokenRequestEndpoint;

public class SwaggerJacksonModule extends SimpleModule {

  public static void maybeRegisterModule(ObjectMapper objectMapper) {
    if (objectMapper.findMixInClassFor(ApiListing.class) == null) {
      objectMapper.registerModule(new SwaggerJacksonModule());
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
  }

  @Override
  public void setupModule(SetupContext module) {
    super.setupModule(module);
    module.setMixInAnnotations(ApiListing.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ResourceListing.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(AllowableListValues.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(AllowableRangeValues.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ApiDescription.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ApiInfo.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ApiKey.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ApiListingReference.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(Authorization.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(AuthorizationCodeGrant.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(AuthorizationScope.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(BasicAuth.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(OAuth.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ImplicitGrant.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(LoginEndpoint.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ModelDto.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ModelPropertyDto.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(DataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ReferenceDataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ContainerDataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(Parameter.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(PrimitiveDataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(PrimitiveFormatDataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(PrimitiveDataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ResponseMessage.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(TokenEndpoint.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(TokenRequestEndpoint.class, CustomizedSwaggerSerializer.class);
  }

  @JsonAutoDetect(
          fieldVisibility = JsonAutoDetect.Visibility.ANY,
          getterVisibility = JsonAutoDetect.Visibility.NONE,
          setterVisibility = JsonAutoDetect.Visibility.NONE,
          creatorVisibility = JsonAutoDetect.Visibility.NONE
  )
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder(alphabetic = true)
  private static class CustomizedSwaggerSerializer {
  }
}
