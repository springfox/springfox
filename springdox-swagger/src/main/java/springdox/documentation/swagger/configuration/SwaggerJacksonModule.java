package springdox.documentation.swagger.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import springdox.documentation.swagger.dto.AllowableListValues;
import springdox.documentation.swagger.dto.AllowableRangeValues;
import springdox.documentation.swagger.dto.ApiDescription;
import springdox.documentation.swagger.dto.ApiInfo;
import springdox.documentation.swagger.dto.ApiKey;
import springdox.documentation.swagger.dto.ApiListing;
import springdox.documentation.swagger.dto.ApiListingReference;
import springdox.documentation.swagger.dto.Authorization;
import springdox.documentation.swagger.dto.AuthorizationCodeGrant;
import springdox.documentation.swagger.dto.AuthorizationScope;
import springdox.documentation.swagger.dto.BasicAuth;
import springdox.documentation.swagger.dto.ContainerDataType;
import springdox.documentation.swagger.dto.DataType;
import springdox.documentation.swagger.dto.ImplicitGrant;
import springdox.documentation.swagger.dto.LoginEndpoint;
import springdox.documentation.swagger.dto.ModelDto;
import springdox.documentation.swagger.dto.ModelPropertyDto;
import springdox.documentation.swagger.dto.OAuth;
import springdox.documentation.swagger.dto.Parameter;
import springdox.documentation.swagger.dto.PrimitiveDataType;
import springdox.documentation.swagger.dto.PrimitiveFormatDataType;
import springdox.documentation.swagger.dto.ReferenceDataType;
import springdox.documentation.swagger.dto.ResourceListing;
import springdox.documentation.swagger.dto.ResponseMessage;
import springdox.documentation.swagger.dto.TokenEndpoint;
import springdox.documentation.swagger.dto.TokenRequestEndpoint;

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
