package springfox.documentation.builders;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.Encoding;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.Representation;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Collection;

import static org.slf4j.LoggerFactory.*;

public class FormParameterSpecificationProvider implements ParameterSpecificationProvider {

  private static final Logger LOGGER = getLogger(FormParameterSpecificationProvider.class);

  @Override
  public ParameterSpecification create(ParameterSpecificationContext context) {
    SimpleParameterSpecification simpleParameter = context.getSimpleParameter();
    ContentSpecification contentParameter = context.getContentParameter();

    ContentSpecification contentSpecification;
    MediaType mediaType = MediaType.ALL;
    if (supportsFormUrlEncoding(context) || noMediaTypeSpecified(context)) {
      mediaType = MediaType.APPLICATION_FORM_URLENCODED;
    } else if (supportsMultipartFormData(context)) {
      mediaType = MediaType.MULTIPART_FORM_DATA;
    }

    if (simpleParameter != null && simpleParameter.getModel() != null) {
      MediaType finalMediaType = mediaType;
      contentSpecification
          = context.getContentSpecificationBuilder()
          .requestBody(true)
          .representation(mediaType)
          .apply(r -> {
            r.model(m -> m.copyOf(simpleParameter.getModel()));
            if (finalMediaType == MediaType.APPLICATION_FORM_URLENCODED) {
              r.clearEncodings();
            } else {
              r.encoding(context.getName())
                  .apply(e ->
                      e.contentType(MediaType.TEXT_PLAIN_VALUE)
                          .style(ParameterStyle.SIMPLE));
            }
          })
          .build();

    } else if (contentParameter != null) {
      Representation representation =
          contentParameter.representationFor(mediaType)
              .orElse(contentParameter.representationFor(MediaType.ALL).orElse(null));
      Collection<Encoding> encodings;
      ModelSpecification model;
      if (representation == null) {
        model = new ModelSpecificationBuilder()
            .name(context.getName())
            .scalarModel(ScalarType.STRING)
            .build();
        contentSpecification = context.getContentSpecificationBuilder()
            .requestBody(true)
            .representation(mediaType)
            .apply(r -> r.model(m -> m.copyOf(model))
                .clearEncodings())
            .build();
      } else {
        model = representation.getModel();
        encodings = representation.getEncodings();
        contentSpecification = context.getContentSpecificationBuilder()
            .requestBody(true)
            .representation(mediaType)
            .apply(r -> {
              r.model(m -> m.copyOf(model));
              encodings.forEach(each -> r.encoding(each.getPropertyRef())
                  .apply(e -> e.copyOf(each)));
            })
            .build();
      }

    } else {
      LOGGER.warn("Parameter should either be a simple or a content type");
      contentSpecification = context.getContentSpecificationBuilder()
          .representation(mediaType)
          .apply(r -> r.model(m -> m.copyOf(new ModelSpecificationBuilder()
              .name(context.getName())
              .scalarModel(ScalarType.STRING)
              .build()))
              .clearEncodings())
          .build();
    }

    return new ParameterSpecification(
        null,
        contentSpecification);
  }

  private boolean noMediaTypeSpecified(ParameterSpecificationContext context) {
    return context.getAccepts().isEmpty();
  }

  private boolean supportsFormUrlEncoding(ParameterSpecificationContext context) {
    return context.getAccepts().stream()
        .anyMatch(mediaType -> mediaType.equalsTypeAndSubtype(MediaType.APPLICATION_FORM_URLENCODED));
  }

  private boolean supportsMultipartFormData(ParameterSpecificationContext context) {
    return context.getAccepts().stream()
        .anyMatch(mediaType -> mediaType.equalsTypeAndSubtype(MediaType.MULTIPART_FORM_DATA)
            || mediaType.equals(MediaType.MULTIPART_MIXED));
  }
}
