package springfox.documentation.builders;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.Representation;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Optional;

import static org.slf4j.LoggerFactory.*;

public class BodyParameterSpecificationProvider implements ParameterSpecificationProvider {
  private static final Logger LOGGER = getLogger(BodyParameterSpecificationProvider.class);


  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    SimpleParameterSpecification simpleParameter = context.getSimpleParameter();
    ContentSpecification contentParameter = context.getContentParameter();

    context.getAccepts().stream()
        .filter(mediaType -> !mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED))
        .forEach(
            each -> {
              if (simpleParameter != null && simpleParameter.getModel() != null) {
                context.getContentSpecificationBuilder()
                    .copyOf(contentParameter)
                    .requestBody(true)
                    .representationBuilderFor(each)
                      .modelSpecificationBuilder(context.getName())
                      .copyOf(simpleParameter.getModel())
                      .yield(RepresentationBuilder.class)
                    .encodings(null);
              } else if (contentParameter != null) {
                Optional<Representation> mediaType = contentParameter.representationFor(each);
                context.getContentSpecificationBuilder()
                    .copyOf(contentParameter)
                    .requestBody(true)
                    .representationBuilderFor(each)
                    .modelSpecificationBuilder(context.getName())
                      .copyOf(mediaType
                        .map(Representation::getModel)
                        .orElse(new ModelSpecificationBuilder("pe_" + context.getName())
                            .name(context.getName())
                            .scalarModel(ScalarType.STRING)
                            .build()))
                      .yield(RepresentationBuilder.class)
                    .encodings(null);

              } else {
                LOGGER.warn("Parameter should either be a simple or a content type");
                context.getContentSpecificationBuilder()
                    .requestBody(true)
                    .representationBuilderFor(each)
                    .modelSpecificationBuilder(context.getName())
                      .copyOf(new ModelSpecificationBuilder("pe_" + context.getName())
                        .name(context.getName())
                        .scalarModel(ScalarType.STRING)
                        .build())
                      .yield(RepresentationBuilder.class)
                    .encodings(null);
              }
            });
    return new ParameterSpecification(null, context.getContentSpecificationBuilder().build());
  }
}
