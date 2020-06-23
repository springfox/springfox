package springfox.documentation.builders;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.Representation;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

public class MultipartFormDataParameterSpecificationProvider implements ParameterSpecificationProvider {
  private static final Logger LOGGER = getLogger(MultipartFormDataParameterSpecificationProvider.class);

  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    SimpleParameterSpecification simpleParameter = context.getSimpleParameter();
    ContentSpecification contentParameter = context.getContentParameter();

    ContentSpecificationBuilder contentSpecificationBuilder = context.getContentSpecificationBuilder();
    if (context.getAccepts().stream()
               .noneMatch(mediaType -> mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED))) {
      if (simpleParameter != null && simpleParameter.getModel() != null) {
        contentSpecificationBuilder
            .copyOf(contentParameter)
            .requestBody(true)
            .representation(MediaType.MULTIPART_FORM_DATA)
            .apply(r -> r.model(m -> m.copyOf(simpleParameter.getModel()))
                         .encodings(Collections.singletonList(
                             new EncodingBuilder(null)
                                 .propertyRef(context.getName())
                                 .contentType(MediaType.TEXT_PLAIN_VALUE)
                                 .build())));
      } else if (contentParameter != null) {
        for (Representation each : contentParameter.getRepresentations()) {
          Optional<Representation> mediaType = contentParameter.representationFor(each.getMediaType());
          contentSpecificationBuilder
              .copyOf(contentParameter)
              .requestBody(true)
              .representation(each.getMediaType())
              .apply(r -> r.model(m -> m.copyOf(
                  mediaType.map(Representation::getModel)
                           .orElse(new ModelSpecificationBuilder()
                                       .name(context.getName())
                                       .scalarModel(ScalarType.STRING)
                                       .build())))
                           .encodings(contentParameter.getRepresentations().stream()
                                                      .flatMap(rep -> rep.getEncodings().stream())
                                                      .collect(Collectors.toList())));
        }
      } else {
        LOGGER.warn("Parameter should either be a simple or a content type");
        contentSpecificationBuilder
            .requestBody(true)
            .representation(MediaType.TEXT_PLAIN)
            .apply(r -> r.model(m -> m.copyOf(new ModelSpecificationBuilder()
                                                  .name(context.getName())
                                                  .scalarModel(ScalarType.STRING)
                                                  .build()))
                         .encodings(null));
      }
    }

    return new ParameterSpecification(
        null,
        contentSpecificationBuilder.build());
  }
}
