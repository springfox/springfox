package springfox.documentation.oas.web;

import io.swagger.v3.oas.models.OpenAPI;

import java.util.Optional;

public class OpenApiTransformationContext<T> {
  private final OpenAPI specification;
  private final T request;

  OpenApiTransformationContext(OpenAPI specification, T request) {
    this.specification = specification;
    this.request = request;
  }

  public OpenAPI getSpecification() {
    return specification;
  }

  public Optional<T> request() {
    return Optional.ofNullable(request);
  }

  public OpenApiTransformationContext<T> next(OpenAPI specification) {
    return new OpenApiTransformationContext<>(specification, request);
  }
}
