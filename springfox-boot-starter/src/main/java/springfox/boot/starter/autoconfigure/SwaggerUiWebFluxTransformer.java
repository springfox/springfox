package springfox.boot.starter.autoconfigure;

import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.resource.ResourceTransformer;
import org.springframework.web.reactive.resource.ResourceTransformerChain;
import org.springframework.web.reactive.resource.TransformedResource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.*;

public class SwaggerUiWebFluxTransformer implements ResourceTransformer {
  private static final Logger LOGGER = getLogger(SwaggerUiWebFluxTransformer.class);
  private final String baseUrl;

  public SwaggerUiWebFluxTransformer(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private String replaceBaseUrl(String html) {
    return html.replace("return/(.*)\\/swagger-ui.html.*/.exec(window.location.href)[1]",
        "return '" + baseUrl + "';");
  }

  @Override
  public Mono<Resource> transform(
      ServerWebExchange exchange,
      Resource resource,
      ResourceTransformerChain transformerChain) {

    return Mono.just(resource)
        .map(r -> {
          AntPathMatcher antPathMatcher = new AntPathMatcher();
          try {
            boolean match = antPathMatcher.match("**/springfox.js", resource.getURL().toString());
            if (match) {
              String html = replaceBaseUrl(StreamUtils.copyToString(r.getInputStream(), StandardCharsets.UTF_8));
              return new TransformedResource(resource, html.getBytes());
            }
            return r;
          } catch (IOException e) {
            throw Exceptions.propagate(e);
          }
        })
        .onErrorResume(e -> {
          LOGGER.error("Unable to transform resource", e);
          return Mono.just(resource);
        });
  }
}
