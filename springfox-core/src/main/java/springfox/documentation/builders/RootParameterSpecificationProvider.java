package springfox.documentation.builders;

import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterType;

import java.util.HashMap;
import java.util.Map;

public class RootParameterSpecificationProvider implements ParameterSpecificationProvider {
  public static final Map<ParameterType, ParameterSpecificationProvider> SPECIFICATION_PROVIDER_LOOKUP =
      new HashMap<ParameterType, ParameterSpecificationProvider>() {{
        put(ParameterType.QUERY, new QueryParameterSpecificationProvider());
        put(ParameterType.COOKIE, new CookieParameterSpecificationProvider());
        put(ParameterType.HEADER, new HeaderParameterSpecificationProvider());
        put(ParameterType.PATH, new PathParameterSpecificationProvider());
        put(ParameterType.FORM, new FormParameterSpecificationProvider());
        put(ParameterType.BODY, new BodyParameterSpecificationProvider());
        put(ParameterType.FORMDATA, new MultipartFormDataParameterSpecificationProvider());
      }};

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
