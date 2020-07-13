package springfox.documentation.swagger2.web;

import io.swagger.models.Swagger;

import java.util.Optional;

public class SwaggerTransformationContext<T> {
  private final Swagger specification;
  private final T request;

  SwaggerTransformationContext(Swagger specification, T request) {
    this.specification = specification;
    this.request = request;
  }

  public Swagger getSpecification() {
    return specification;
  }

  public Optional<T> request() {
    return Optional.ofNullable(request);
  }

  public SwaggerTransformationContext<T> next(Swagger specification) {
    return new SwaggerTransformationContext<>(specification, request);
  }

}
