package com.mangofactory.swagger.models.dto.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mangofactory.swagger.models.dto.AllowableListValues;
import com.mangofactory.swagger.models.dto.AllowableRangeValues;
import com.mangofactory.swagger.models.dto.ApiDescription;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.models.dto.ApiKey;
import com.mangofactory.swagger.models.dto.ApiListing;
import com.mangofactory.swagger.models.dto.ApiListingReference;
import com.mangofactory.swagger.models.dto.Authorization;
import com.mangofactory.swagger.models.dto.AuthorizationCodeGrant;
import com.mangofactory.swagger.models.dto.AuthorizationScope;
import com.mangofactory.swagger.models.dto.BasicAuth;
import com.mangofactory.swagger.models.dto.ContainerDataType;
import com.mangofactory.swagger.models.dto.DataType;
import com.mangofactory.swagger.models.dto.ImplicitGrant;
import com.mangofactory.swagger.models.dto.LoginEndpoint;
import com.mangofactory.swagger.models.dto.Model;
import com.mangofactory.swagger.models.dto.ModelProperty;
import com.mangofactory.swagger.models.dto.ModelRef;
import com.mangofactory.swagger.models.dto.OAuth;
import com.mangofactory.swagger.models.dto.Parameter;
import com.mangofactory.swagger.models.dto.PrimitiveDataType;
import com.mangofactory.swagger.models.dto.PrimitiveFormatDataType;
import com.mangofactory.swagger.models.dto.ResourceListing;
import com.mangofactory.swagger.models.dto.ResponseMessage;
import com.mangofactory.swagger.models.dto.TokenEndpoint;
import com.mangofactory.swagger.models.dto.TokenRequestEndpoint;

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
    module.setMixInAnnotations(Model.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ModelProperty.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(DataType.class, CustomizedSwaggerSerializer.class);
    module.setMixInAnnotations(ModelRef.class, CustomizedSwaggerSerializer.class);
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
