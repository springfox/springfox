package com.mangofactory.documentation.swagger2.mappers;

import com.mangofactory.documentation.service.AuthorizationType;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;

interface SecuritySchemeFactory {
  SecuritySchemeDefinition create(AuthorizationType input);
}
