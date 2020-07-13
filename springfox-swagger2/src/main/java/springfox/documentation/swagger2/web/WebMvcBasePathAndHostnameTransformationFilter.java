package springfox.documentation.swagger2.web;

import io.swagger.models.Swagger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponents;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.util.StringUtils.*;
import static springfox.documentation.swagger.common.HostNameProvider.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcBasePathAndHostnameTransformationFilter implements WebMvcSwaggerTransformationFilter {
  private final Environment env;

  public WebMvcBasePathAndHostnameTransformationFilter(Environment env) {
    this.env = env;
  }

  @Override
  public Swagger transform(SwaggerTransformationContext<HttpServletRequest> context) {
    Swagger swagger = context.getSpecification();
    String hostNameOverride =
        env.getProperty(
            "springfox.documentation.swagger.v2.host",
            "DEFAULT");
    context.request().ifPresent(servletRequest -> {
      UriComponents uriComponents = componentsFrom(servletRequest, swagger.getBasePath());
      String basePath = isEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath();
      swagger.basePath(basePath.replace(servletRequest.getContextPath(), ""));
      if (isEmpty(swagger.getHost())) {
        swagger.host(hostName(uriComponents, hostNameOverride));
      }
    });
    return swagger;
  }

  private String hostName(
      UriComponents uriComponents,
      String hostNameOverride) {
    if ("DEFAULT".equals(hostNameOverride)) {
      String host = uriComponents.getHost();
      int port = uriComponents.getPort();
      if (port > -1) {
        return String.format("%s:%d", host, port);
      }
      return host;
    }
    return hostNameOverride;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return delimiter == DocumentationType.SWAGGER_2;
  }
}
