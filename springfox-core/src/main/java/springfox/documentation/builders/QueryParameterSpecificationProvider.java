package springfox.documentation.builders;

import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.service.CollectionFormat;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Arrays;
import java.util.List;

class QueryParameterSpecificationProvider implements ParameterSpecificationProvider {
  static final List<ParameterStyle> VALID_COLLECTION_STYLES =
      Arrays.asList(
          ParameterStyle.FORM,
          ParameterStyle.SPACEDELIMITED,
          ParameterStyle.PIPEDELIMITED
      );
  static final List<ParameterStyle> VALID_OBJECT_STYLES =
      Arrays.asList(
          ParameterStyle.FORM,
          ParameterStyle.DEEPOBJECT
      );

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity"})
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
              .explode(simpleParameter.getExplode())
              .style(ParameterStyle.FORM)
              .build();
        } else if (model.getCollection().isPresent()) {
          ParameterStyle style =
              VALID_COLLECTION_STYLES.contains(simpleParameter.getStyle())
                  ? simpleParameter.getStyle()
                  : simpleParameter.nullSafeIsExplode()
                  ? ParameterStyle.FORM
                  : ParameterStyle.PIPEDELIMITED;

          validSimpleParameter =
              context.getSimpleParameterSpecificationBuilder()
                  .copyOf(simpleParameter)
                  .explode(simpleParameter.getExplode())
                  .style(style)
                  .collectionFormat(CollectionFormat.MULTI)
                  .build();
        }
      }
      if (validSimpleParameter == null) {
        ParameterStyle style = VALID_OBJECT_STYLES.contains(simpleParameter.getStyle())
            ? simpleParameter.getStyle()
            : simpleParameter.nullSafeIsExplode() ? ParameterStyle.FORM : ParameterStyle.DEEPOBJECT;
        validSimpleParameter = context.getSimpleParameterSpecificationBuilder()
            .copyOf(simpleParameter)
            .explode(style == ParameterStyle.DEEPOBJECT ? Boolean.TRUE : simpleParameter.getExplode())
            .style(style)
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
