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
            .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
            .modelSpecificationBuilder()
            .copyOf(simpleParameter.getModel())
            .yield(RepresentationBuilder.class)
            .encodings(Collections.singletonList(
                new EncodingBuilder(null)
                    .propertyRef(context.getName())
                    .contentType(MediaType.TEXT_PLAIN_VALUE)
                    .build()));
      } else if (contentParameter != null) {
        for (Representation each : contentParameter.getRepresentations()) {
          Optional<Representation> mediaType = contentParameter.representationFor(each.getMediaType());
          contentSpecificationBuilder
              .copyOf(contentParameter)
              .requestBody(true)
              .representationBuilderFor(each.getMediaType())
              .modelSpecificationBuilder()
              .copyOf(
                  mediaType.map(Representation::getModel)
                           .orElse(new ModelSpecificationBuilder()
                                       .name(context.getName())
                                       .scalarModel(ScalarType.STRING)
                                       .build()))
              .yield(RepresentationBuilder.class)
              .encodings(contentParameter.getRepresentations().stream()
                                         .flatMap(r -> r.getEncodings().stream())
                                         .collect(Collectors.toList()));
        }
      } else {
        LOGGER.warn("Parameter should either be a simple or a content type");
        contentSpecificationBuilder
            .requestBody(true)
            .representationBuilderFor(MediaType.TEXT_PLAIN)
            .modelSpecificationBuilder()
            .copyOf(new ModelSpecificationBuilder()
                        .name(context.getName())
                        .scalarModel(ScalarType.STRING)
                        .build())
            .yield(RepresentationBuilder.class)
            .encodings(null);
      }
    }
    
    return new ParameterSpecification(
        null,
        contentSpecificationBuilder.build());
  }
}
