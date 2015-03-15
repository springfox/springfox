package springdox.documentation.swagger2.mappers;

import com.wordnik.swagger.models.auth.BasicAuthDefinition;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import springdox.documentation.service.AuthorizationType;

class BasicAuthFactory implements SecuritySchemeFactory {
  @Override
  public SecuritySchemeDefinition create(AuthorizationType input) {
    return new BasicAuthDefinition();
  }
}
