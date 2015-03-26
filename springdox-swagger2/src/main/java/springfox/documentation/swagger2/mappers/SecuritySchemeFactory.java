package springfox.documentation.swagger2.mappers;

import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import springfox.documentation.service.AuthorizationType;

interface SecuritySchemeFactory {
  SecuritySchemeDefinition create(AuthorizationType input);
}
