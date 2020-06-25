package springfox.documentation.builders;

import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Arrays;
import java.util.List;

public class PathParameterSpecificationProvider implements ParameterSpecificationProvider {
  static final List<ParameterStyle> VALID_COLLECTION_STYLES =
      Arrays.asList(
          ParameterStyle.MATRIX,
          ParameterStyle.LABEL,
          ParameterStyle.SIMPLE
      );
  static final List<ParameterStyle> VALID_OBJECT_STYLES =
      Arrays.asList(
          ParameterStyle.MATRIX,
          ParameterStyle.LABEL
      );

  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    SimpleParameterSpecification simpleParameter = context.getSimpleParameter();
    SimpleParameterSpecification validSimpleParameter = null;
    ContentSpecification validContentEncoding = null;
    if (simpleParameter != null &&  simpleParameter.getModel() != null) {
      if (simpleParameter.getModel().getScalar().isPresent()) {
        validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
            .copyOf(simpleParameter)
            .explode(null)
            .style(ParameterStyle.SIMPLE)
            .build();
      } else if (simpleParameter.getModel().getCollection().isPresent()) {
        validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
            .copyOf(simpleParameter)
            .explode(null)
            .style(VALID_COLLECTION_STYLES.contains(simpleParameter.getStyle())
                ? simpleParameter.getStyle()
                : ParameterStyle.SIMPLE)
            .collectionFormat(null)
            .build();
      } else {
        validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
            .copyOf(simpleParameter)
            .explode(null)
            .style(VALID_OBJECT_STYLES.contains(simpleParameter.getStyle())
                ? simpleParameter.getStyle()
                : ParameterStyle.SIMPLE)
            .collectionFormat(null)
            .build();
      }
    }
    if (context.getContentParameter() != null) {
      validContentEncoding = context.getContentSpecificationBuilder()
          .copyOf(context.getContentParameter())
          .build();
    }
    return new ParameterSpecification(
        validSimpleParameter,
        validContentEncoding);
  }
}
