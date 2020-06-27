package springfox.boot.starter.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static springfox.documentation.builders.BuilderDefaults.*;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(
    value = "springfox.documentation.swagger-ui.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SwaggerUiWebFluxConfiguration {
  @Value("${springfox.documentation.swagger-ui.base-url:}")
  private String swaggerBaseUrl;

  @Bean
  public SwaggerUiWebFluxConfigurer swaggerUiWebfluxConfigurer() {
    return new SwaggerUiWebFluxConfigurer(fixup(swaggerBaseUrl));
  }

  @Bean
  public WebFilter uiForwarder() {
    return new CustomWebFilter();
  }

  private String fixup(String swaggerBaseUrl) {
    return StringUtils.trimTrailingCharacter(nullToEmpty(swaggerBaseUrl), '/');
  }

  private static class CustomWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      String path = exchange.getRequest().getURI().getPath();
      if (path.matches(".*/swagger-ui/")) {
        return chain.filter(exchange.mutate().request(exchange.getRequest()
            .mutate()
            .path(StringUtils.trimTrailingCharacter(path, '/') + "/index.html")
            .build())
            .build());
      }
      return chain.filter(exchange);
    }
  }
}
