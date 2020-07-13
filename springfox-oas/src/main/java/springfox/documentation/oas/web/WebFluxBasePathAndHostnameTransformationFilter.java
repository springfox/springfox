package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import springfox.documentation.spi.DocumentationType;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static springfox.documentation.oas.web.SpecGeneration.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxBasePathAndHostnameTransformationFilter implements WebFluxOpenApiTransformationFilter {
  private final String requestPrefix;

  public WebFluxBasePathAndHostnameTransformationFilter(@Value(OPEN_API_SPECIFICATION_PATH) String oasPath) {
    this.requestPrefix = StringUtils.trimTrailingCharacter(oasPath, '/');
  }

  @Override
  public OpenAPI transform(OpenApiTransformationContext<ServerHttpRequest> context) {
    OpenAPI openApi = context.getSpecification();
    context.request().ifPresent(request -> {
      String requestUrl = decode(new ForwardedHeaderTransformer().apply(request).getURI().toString());
      openApi.servers(Collections.singletonList(inferredServer(requestPrefix, requestUrl)));
    });
    return openApi;
  }

  protected String decode(String requestURI) {
    try {
      return URLDecoder.decode(requestURI, StandardCharsets.UTF_8.toString());
    } catch (UnsupportedEncodingException e) {
      return requestURI;
    }
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return delimiter == DocumentationType.OAS_30;
  }
}
