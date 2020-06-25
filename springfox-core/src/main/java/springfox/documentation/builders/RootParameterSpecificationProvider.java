package springfox.documentation.builders;

import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterType;

import java.util.HashMap;
import java.util.Map;

public class RootParameterSpecificationProvider implements ParameterSpecificationProvider {
  static final Map<ParameterType, ParameterSpecificationProvider> SPECIFICATION_PROVIDER_LOOKUP = new HashMap<>();

  static {
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.QUERY, new QueryParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.COOKIE, new CookieParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.HEADER, new HeaderParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.PATH, new PathParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.FORM, new FormParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.BODY, new BodyParameterSpecificationProvider());
    SPECIFICATION_PROVIDER_LOOKUP.put(ParameterType.FORMDATA, new MultipartFormDataParameterSpecificationProvider());
  }

  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    if (context.getSimpleParameterSpecificationBuilder() == null
        && context.getContentSpecificationBuilder() == null) {
      throw new IllegalStateException("Parameter should either be a simple parameter or content");
    }
    return SPECIFICATION_PROVIDER_LOOKUP.getOrDefault(
        context.getIn(),
        new PassThroughSpecificationProvider()).create(context);
  }

  private static class PassThroughSpecificationProvider implements ParameterSpecificationProvider {
    @Override
    public ParameterSpecification create(ParameterSpecificationContext context) {
      return new ParameterSpecification(context.getSimpleParameter(), context.getContentParameter());
    }
  }
}
