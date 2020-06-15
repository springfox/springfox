package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.mapstruct.Mapper;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.OAuth2Scheme;
import springfox.documentation.service.OpenIdConnectScheme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public class SecuritySchemeMapper {
  Map<String, SecurityScheme> map(List<springfox.documentation.service.SecurityScheme> schemes) {
    Map<String, SecurityScheme> mapped = new HashMap<>();
    schemes.forEach(each -> mapScheme(mapped, each));
    return mapped;
  }

  void mapScheme(Map<String, SecurityScheme> map, springfox.documentation.service.SecurityScheme scheme) {
    SecurityScheme mapped = null;
    SecurityScheme securityScheme = new SecurityScheme()
        .extensions(new VendorExtensionsMapper().mapExtensions(scheme.getVendorExtensions()));
    if (scheme instanceof HttpAuthenticationScheme) {
      mapped = securityScheme
          .type(SecurityScheme.Type.HTTP)
          .description(scheme.getDescription())
          .bearerFormat(((HttpAuthenticationScheme) scheme).getBearerFormat())
          .scheme(((HttpAuthenticationScheme) scheme).getScheme());
    } else if (scheme instanceof OAuth2Scheme) {
      OAuthFlows flows = new OAuthFlows();
      Scopes scopes = new Scopes();
      ((OAuth2Scheme) scheme).getScopes()
                             .forEach(s -> scopes.addString(s.getScope(), s.getDescription()));
      OAuthFlow flow = new OAuthFlow()
          .authorizationUrl(((OAuth2Scheme) scheme).getAuthorizationUrl())
          .refreshUrl(((OAuth2Scheme) scheme).getRefreshUrl())
          .tokenUrl(((OAuth2Scheme) scheme).getTokenUrl())
          .scopes(scopes);
      switch (((OAuth2Scheme) scheme).getFlowType()) {
        case "password":
          flows.password(flow);
          break;
        case "clientCredentials":
          flows.clientCredentials(flow);
          break;
        case "authorizationCode":
          flows.authorizationCode(flow);
          break;
        case "implicit":
        default:
          flows.implicit(flow);
          break;
      }
      mapped = securityScheme
          .type(SecurityScheme.Type.OAUTH2)
          .description(scheme.getDescription())
          .flows(flows);
    } else if (scheme instanceof ApiKey) {
      mapped = securityScheme
          .type(SecurityScheme.Type.APIKEY)
          .name(scheme.getName())
          .in(mapIn(((ApiKey) scheme).getPassAs()));
    } else if (scheme instanceof OpenIdConnectScheme) {
      mapped = securityScheme
          .type(SecurityScheme.Type.OPENIDCONNECT)
          .name(scheme.getName())
          .openIdConnectUrl(((OpenIdConnectScheme) scheme).getOpenIdConnectUrl());
    }
    if (mapped != null) {
      map.put(scheme.getName(), mapped);
    }
  }

  private SecurityScheme.In mapIn(String passAs) {
    switch (passAs.toLowerCase()) {
      case "header":
        return SecurityScheme.In.HEADER;
      case "cookie":
        return SecurityScheme.In.COOKIE;
      case "query":
      default:
        return SecurityScheme.In.QUERY;
    }
  }
}
