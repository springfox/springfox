package springfox.documentation.builders;

import springfox.documentation.service.ParameterSpecification;

@FunctionalInterface
public interface ParameterSpecificationProvider {
  ParameterSpecification create(ParameterSpecificationContext context);
}
