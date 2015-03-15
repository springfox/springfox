package springdox.documentation.swagger2.mappers;

import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import springdox.documentation.service.AuthorizationType;

interface SecuritySchemeFactory {
  SecuritySchemeDefinition create(AuthorizationType input);
}
