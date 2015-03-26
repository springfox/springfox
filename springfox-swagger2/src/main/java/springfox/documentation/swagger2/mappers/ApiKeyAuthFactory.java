package springfox.documentation.swagger2.mappers;

import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition;
import com.wordnik.swagger.models.auth.In;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationType;

class ApiKeyAuthFactory implements SecuritySchemeFactory {
  @Override
  public SecuritySchemeDefinition create(AuthorizationType input) {
    ApiKey apiKey = (ApiKey) input;
    ApiKeyAuthDefinition definition = new ApiKeyAuthDefinition();
    definition.name(apiKey.getName()).in(In.forValue(apiKey.getPassAs()));
    return definition;
  }
}
