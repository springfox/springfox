package springfox.documentation.swagger2.web;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface WebFluxSwaggerTransformationFilter extends SwaggerTransformationFilter<ServerHttpRequest> {
}
