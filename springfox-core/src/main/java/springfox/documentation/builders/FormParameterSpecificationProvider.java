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
      RepresentationBuilder representationBuilder = context.getContentSpecificationBuilder()
                                                           .requestBody(true)
                                                           .representationBuilderFor(mediaType)
                                                           .modelSpecificationBuilder(context.getName())
                                                           .copyOf(simpleParameter.getModel())
                                                           .yield(RepresentationBuilder.class);
      if (mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
        contentSpecification = representationBuilder
            .encodings(null)
            .yield()
            .build();
      } else {
        contentSpecification = representationBuilder
            .encodingForProperty(context.getName())
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .style(ParameterStyle.SIMPLE)
            .yield()
            .yield()
            .build();
      }
    } else if (contentParameter != null) {
      Representation representation =
          contentParameter.representationFor(mediaType)
                          .orElse(contentParameter.representationFor(MediaType.ALL).orElse(null));
      Collection<Encoding> encodings;
      ModelSpecification model;
      if (representation == null) {
        model = new ModelSpecificationBuilder("pe_" + context.getName())
            .name(context.getName())
            .scalarModel(ScalarType.STRING)
            .build();
        encodings = null;
      } else {
        model = representation.getModel();
        encodings = representation.getEncodings();
      }
      contentSpecification = context.getContentSpecificationBuilder()
                                    .requestBody(true)
                                    .representationBuilderFor(mediaType)
                                    .modelSpecificationBuilder(context.getName())
                                    .copyOf(model)
                                    .yield(RepresentationBuilder.class)
                                    .encodings(encodings)
                                    .yield()
                                    .build();
    } else {
      LOGGER.warn("Parameter should either be a simple or a content type");
      contentSpecification = context.getContentSpecificationBuilder()
                                    .representationBuilderFor(mediaType)
                                    .modelSpecificationBuilder(context.getName())
                                    .copyOf(new ModelSpecificationBuilder("pe_" + context.getName())
                                                .name(context.getName())
                                                .scalarModel(ScalarType.STRING)
                                                .build())
                                    .yield(RepresentationBuilder.class)
                                    .encodings(null)
                                    .yield()
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
