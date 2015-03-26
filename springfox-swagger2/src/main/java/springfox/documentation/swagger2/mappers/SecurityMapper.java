package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wordnik.swagger.models.auth.SecuritySchemeDefinition;
import org.mapstruct.Mapper;
import springfox.documentation.service.AuthorizationType;
import springfox.documentation.service.ResourceListing;

import java.util.Map;

@Mapper
public class SecurityMapper {
  private Map<String, SecuritySchemeFactory> factories = ImmutableMap.<String, SecuritySchemeFactory>builder()
          .put("oauth2", new OAuth2AuthFactory())
          .put("apiKey", new ApiKeyAuthFactory())
          .put("basic", new BasicAuthFactory())
          .build();

  protected Map<String, SecuritySchemeDefinition> toSecuritySchemeDefinitions(ResourceListing from) {
    return Maps.transformValues(Maps.uniqueIndex(from.getAuthorizations(), schemeName()), toSecuritySchemeDefinition());
  }

  private Function<AuthorizationType, String> schemeName() {
    return new Function<AuthorizationType, String>() {
      @Override
      public String apply(AuthorizationType input) {
        return input.getName();
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

}
