package com.mangofactory.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.documentation.service.ApiKey;
import com.mangofactory.documentation.service.AuthorizationCodeGrant;
import com.mangofactory.documentation.service.AuthorizationScope;
import com.mangofactory.documentation.service.AuthorizationType;
import com.mangofactory.documentation.service.GrantType;
import com.mangofactory.documentation.service.ImplicitGrant;
import com.mangofactory.documentation.service.OAuth;
import com.mangofactory.documentation.service.ResourceListing;
import com.wordnik.swagger.models.auth.ApiKeyAuthDefinition;
import com.wordnik.swagger.models.auth.BasicAuthDefinition;
import com.wordnik.swagger.models.auth.In;
import com.wordnik.swagger.models.auth.OAuth2Definition;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import org.mapstruct.Mapper;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Mapper
public class SecurityMapper {
  private Map<String, SecuritySchemeFactory> factories = ImmutableMap.<String, SecuritySchemeFactory>builder()
          .put("oauth2", new OAuth2AuthFactory())
          .put("apiKey", new ApiKeyAuthFactory())
          .put("basic", new BasicAuthFactory())
          .build();

  protected Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ResourceListing from) {
    return transformValues(uniqueIndex(from.getAuthorizations(), authorizationType()), toSecuritySchemeDefinition());
  }                                           

  protected Function<AuthorizationType, String> authorizationType() {
    return new Function<AuthorizationType, String>() {
      @Override
      public String apply(AuthorizationType input) {
        return input.getType();
      }
    };
  }

  private Function<AuthorizationType, SecuritySchemeDefinition> toSecuritySchemeDefinition() {
    return new Function<AuthorizationType, SecuritySchemeDefinition>() {
      @Override
      public SecuritySchemeDefinition apply(AuthorizationType input) {
        return factories.get(input.getType()).create(input);
      }
    };
  }

  private interface SecuritySchemeFactory {
    SecuritySchemeDefinition create(AuthorizationType input);
  }

  private class OAuth2AuthFactory implements SecuritySchemeFactory {
    @Override
    public SecuritySchemeDefinition create(AuthorizationType input) {
      OAuth oAuth = (OAuth) input;
      OAuth2Definition definition = new OAuth2Definition();
      for (GrantType each : oAuth.getGrantTypes()) {
        if ("authorization_code".equals(each.getType())) {
          definition.accessCode(((AuthorizationCodeGrant)each).getTokenRequestEndpoint().getUrl(), 
                  ((AuthorizationCodeGrant)each).getTokenEndpoint().getUrl());
        } else if ("implicit".equals(each.getType())) {
          definition.implicit(((ImplicitGrant)each).getLoginEndpoint().getUrl());
//        } else if ("application".equals(each.getType())) {
//          TODO: swagger 1 doesnt not support this
//        } else if ("password".equals(each.getType())) {
//          TODO: swagger 1 doesnt support this
        }
      }
      for (AuthorizationScope each : oAuth.getScopes()) {
        definition.addScope(each.getScope(), each.getDescription());   
      }
      return definition;
    }
  }

  private class ApiKeyAuthFactory implements SecuritySchemeFactory {
    @Override
    public SecuritySchemeDefinition create(AuthorizationType input) {
      ApiKey apiKey = (ApiKey) input;
      ApiKeyAuthDefinition definition = new ApiKeyAuthDefinition();
      definition.name(apiKey.getName()).in(In.forValue(apiKey.getPassAs()));
      return definition;
    }
  }

  private class BasicAuthFactory implements SecuritySchemeFactory {
    @Override
    public SecuritySchemeDefinition create(AuthorizationType input) {
      return new BasicAuthDefinition();
    }
  }
}
