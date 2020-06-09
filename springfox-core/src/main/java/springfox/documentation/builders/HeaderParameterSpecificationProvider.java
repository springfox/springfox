package springfox.documentation.builders;

import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.SimpleParameterSpecification;

public class HeaderParameterSpecificationProvider implements ParameterSpecificationProvider {
  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    SimpleParameterSpecification simpleParameter = context.getSimpleParameter();
    SimpleParameterSpecification validSimpleParameter = null;
    ContentSpecification validContentEncoding = null;
    if (simpleParameter != null) {
      ModelSpecification model = simpleParameter.getModel();
      if (model != null) {
        if (model.getScalar().isPresent()) {
          validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
              .copyOf(simpleParameter)
              .explode(null)
              .style(null)
              .build();
        } else if (model.getCollection().isPresent()) {
          validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
              .copyOf(simpleParameter)
              .explode(simpleParameter.getExplode())
              .style(ParameterStyle.SIMPLE)
              .collectionFormat(CollectionFormat.CSV)
              .build();
        } else {
          validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
              .copyOf(simpleParameter)
              .explode(null)
              .style(ParameterStyle.SIMPLE)
              .collectionFormat(null)
              .build();
        }
      } else {
        validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
            .copyOf(simpleParameter)
            .explode(null)
            .style(ParameterStyle.SIMPLE)
            .collectionFormat(null)
            .build();
      }
    }

    if (context.getContentParameter() != null) {
      validContentEncoding = context.getContentSpecificationBuilder()
                                    .copyOf(context.getContentParameter())
                                    .build();
    }
    return new
        ParameterSpecification(
        validSimpleParameter,
        validContentEncoding);
  }
}
