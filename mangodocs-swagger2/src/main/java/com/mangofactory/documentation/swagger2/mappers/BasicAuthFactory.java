package com.mangofactory.documentation.swagger2.mappers;

import com.mangofactory.documentation.service.AuthorizationType;
import com.wordnik.swagger.models.auth.BasicAuthDefinition;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;

class BasicAuthFactory implements SecuritySchemeFactory {
  @Override
  public SecuritySchemeDefinition create(AuthorizationType input) {
    return new BasicAuthDefinition();
  }
}
