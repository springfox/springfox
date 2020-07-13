package springfox.documentation.oas.web;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface WebFluxOpenApiTransformationFilter extends OpenApiTransformationFilter<ServerHttpRequest> {
}
