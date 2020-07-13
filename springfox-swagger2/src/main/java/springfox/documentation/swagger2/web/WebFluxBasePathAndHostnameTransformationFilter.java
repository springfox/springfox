package springfox.documentation.swagger2.web;

import io.swagger.models.Swagger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import springfox.documentation.spi.DocumentationType;

import static org.springframework.util.StringUtils.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxBasePathAndHostnameTransformationFilter implements WebFluxSwaggerTransformationFilter {

  @Override
  public Swagger transform(SwaggerTransformationContext<ServerHttpRequest> context) {
    Swagger swagger = context.getSpecification();
    context.request().ifPresent(request -> {
      swagger.basePath(isEmpty(request.getPath().contextPath().value())
          ? "/"
          : request.getPath().contextPath().value());
      if (isEmpty(swagger.getHost())) {
        swagger.host(request.getURI().getAuthority());
      }
    });
    return swagger;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return delimiter == DocumentationType.SWAGGER_2;
  }
}
