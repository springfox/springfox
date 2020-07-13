package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.slf4j.LoggerFactory.*;
import static springfox.documentation.oas.web.SpecGeneration.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcBasePathAndHostnameTransformationFilter implements WebMvcOpenApiTransformationFilter {
  private static final Logger LOGGER = getLogger(WebMvcBasePathAndHostnameTransformationFilter.class);
  private final String requestPrefix;

  public WebMvcBasePathAndHostnameTransformationFilter(@Value(OPEN_API_SPECIFICATION_PATH) String oasPath) {
    this.requestPrefix = StringUtils.trimTrailingCharacter(oasPath, '/');
  }

  @Override
  public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
    OpenAPI openApi = context.getSpecification();
    context.request().ifPresent(servletRequest -> {
      ForwardedHeaderExtractingRequest filter
          = new ForwardedHeaderExtractingRequest(servletRequest, new UrlPathHelper());
      openApi.servers(Collections.singletonList(inferredServer(requestPrefix, filter.adjustedRequestURL())));
    });
    return openApi;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return delimiter == DocumentationType.OAS_30;
  }
}
