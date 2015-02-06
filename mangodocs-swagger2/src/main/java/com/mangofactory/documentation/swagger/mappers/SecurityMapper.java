package com.mangofactory.documentation.swagger.mappers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.mangofactory.documentation.service.ApiListing;
import com.mangofactory.documentation.service.Authorization;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import org.mapstruct.Mapper;

import java.util.Map;

import static com.google.common.collect.Maps.*;

@Mapper
public class SecurityMapper {
  private Map<String, SecuritySchemeFactory> factories = ImmutableMap.<String, SecuritySchemeFactory>builder().build();
  //          .put("oauth2", new OAuth2AuthFactory()).build();
//          .put("apiKey", new ApiKeyAuthFactory())
//          .put("basic", new BasicAuthFactory()).build();
  protected Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ApiListing from) {
//    return transformValues(uniqueIndex(from.getAuthorizations(), authorizationType()), toSecuritySchemeDefinition());
    return newHashMap();
  }

  private Function<Authorization, SecuritySchemeDefinition> toSecuritySchemeDefinition() {
    return new Function<Authorization, SecuritySchemeDefinition>() {
      @Override
      public SecuritySchemeDefinition apply(Authorization input) {
        return factories.get(input.getType()).create(input);
      }
    };
  }

  protected Function<Authorization,String> authorizationType() {
    return new Function<Authorization, String>() {
      @Override
      public String apply(Authorization input) {
        return input.getType();
      }
    };
  }

  private interface SecuritySchemeFactory {
    SecuritySchemeDefinition create(Authorization input);
  }

}
