package springfox.documentation.builders;

import org.springframework.http.MediaType;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ParameterSpecificationContext {
  private final String name;
  private final ParameterType in;
  private final SimpleParameterSpecification simpleParameter;
  private final ContentSpecification contentParameter;
  private final SimpleParameterSpecificationBuilder simpleParameterSpecificationBuilder;
  private final ContentSpecificationBuilder contentSpecificationBuilder;
  private final Set<MediaType> accepts = new HashSet<>();

  public ParameterSpecificationContext(
      String name,
      ParameterType in,
      Collection<MediaType> accepts,
      SimpleParameterSpecification simpleParameter,
      ContentSpecification contentParameter,
      SimpleParameterSpecificationBuilder simpleParameterSpecificationBuilder,
      ContentSpecificationBuilder contentSpecificationBuilder) {
    this.name = name;
    this.in = in;
    this.simpleParameter = simpleParameter;
    this.contentParameter = contentParameter;
    this.simpleParameterSpecificationBuilder = simpleParameterSpecificationBuilder;
    this.contentSpecificationBuilder = contentSpecificationBuilder;
    this.accepts.addAll(accepts);
  }

  public ParameterType getIn() {
    return in;
  }

  public SimpleParameterSpecification getSimpleParameter() {
    return simpleParameter;
  }

  public ContentSpecification getContentParameter() {
    return contentParameter;
  }

  public Collection<MediaType> getAccepts() {
    return accepts;
  }

  public SimpleParameterSpecificationBuilder getSimpleParameterSpecificationBuilder() {
    return simpleParameterSpecificationBuilder;
  }

  public ContentSpecificationBuilder getContentSpecificationBuilder() {
    return contentSpecificationBuilder;
  }

  public String getName() {
    return name;
  }
}
