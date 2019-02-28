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

package springfox.documentation.swagger1.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.swagger1.dto.AllowableListValues;
import springfox.documentation.swagger1.dto.AllowableRangeValues;
import springfox.documentation.swagger1.dto.ApiDescription;
import springfox.documentation.swagger1.dto.ApiInfo;
import springfox.documentation.swagger1.dto.ApiKey;
import springfox.documentation.swagger1.dto.ApiListing;
import springfox.documentation.swagger1.dto.ApiListingReference;
import springfox.documentation.swagger1.dto.Authorization;
import springfox.documentation.swagger1.dto.AuthorizationCodeGrant;
import springfox.documentation.swagger1.dto.AuthorizationScope;
import springfox.documentation.swagger1.dto.BasicAuth;
import springfox.documentation.swagger1.dto.ContainerDataType;
import springfox.documentation.swagger1.dto.DataType;
import springfox.documentation.swagger1.dto.ImplicitGrant;
import springfox.documentation.swagger1.dto.LoginEndpoint;
import springfox.documentation.swagger1.dto.ModelDto;
import springfox.documentation.swagger1.dto.ModelPropertyDto;
import springfox.documentation.swagger1.dto.OAuth;
import springfox.documentation.swagger1.dto.Parameter;
import springfox.documentation.swagger1.dto.PrimitiveDataType;
import springfox.documentation.swagger1.dto.PrimitiveFormatDataType;
import springfox.documentation.swagger1.dto.ReferenceDataType;
import springfox.documentation.swagger1.dto.ResourceListing;
import springfox.documentation.swagger1.dto.ResponseMessage;
import springfox.documentation.swagger1.dto.TokenEndpoint;
import springfox.documentation.swagger1.dto.TokenRequestEndpoint;

public class SwaggerJacksonModule extends SimpleModule implements JacksonModuleRegistrar {

  public void maybeRegisterModule(ObjectMapper objectMapper) {
    if (isModuleSetup(objectMapper)) {
      objectMapper.registerModule(this);
      objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }
  }

  private static boolean isModuleSetup(ObjectMapper objectMapper) {
    return objectMapper.findMixInClassFor(ApiListing.class) == null;
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
