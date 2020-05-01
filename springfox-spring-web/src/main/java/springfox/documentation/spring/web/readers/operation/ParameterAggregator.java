package springfox.documentation.spring.web.readers.operation;

import springfox.documentation.service.RequestParameter;

import java.util.Collection;

public interface ParameterAggregator {
  Collection<RequestParameter> aggregate(Collection<RequestParameter> parameters);
}
