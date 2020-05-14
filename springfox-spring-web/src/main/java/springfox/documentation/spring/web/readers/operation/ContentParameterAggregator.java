package springfox.documentation.spring.web.readers.operation;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.builders.RepresentationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ModelKeyBuilder;
import springfox.documentation.schema.QualifiedModelName;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.service.Encoding;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContentParameterAggregator implements ParameterAggregator {
  public Collection<RequestParameter> aggregate(Collection<RequestParameter> parameters) {
    RequestParameterBuilder builder = new RequestParameterBuilder();

    // @formatter:off
    parameters.stream()
        .filter(p -> p.getIn() == ParameterType.FORM)
        .forEach(each -> builder
            .name("body")
            .in(ParameterType.FORMDATA)
            .contentSpecificationBuilder()
                .requestBody(true)
                .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
                  .modelSpecificationBuilder()
                  .compoundModelBuilder()
                    .modelKey(new ModelKeyBuilder()
                                  .qualifiedModelName(
                                      new QualifiedModelName("io.springfox",
                                                             each.getName() + "Aggregate"))
                                  .viewDiscriminator(null)
                                  .validationGroupDiscriminators(new ArrayList<>())
                                  .isResponse(false)
                                  .build())
                    .properties(properties(each))
                    .yield()
                  .yield(RepresentationBuilder.class)
                  .encodingForProperty(each.getName())
                  .copyOf(encoding(each, MediaType.TEXT_PLAIN))
                .yield()
              .yield()
            .yield()
            .build());

    parameters.stream()
        .filter(p -> p.getIn() == ParameterType.FORMDATA
            && p.getParameterSpecification().getContent()
            .map(c -> c.getRepresentations().stream()
                .anyMatch(m -> m.getMediaType() == MediaType.MULTIPART_FORM_DATA
                    || m.getMediaType() == MediaType.MULTIPART_MIXED))
            .orElse(false))
        .forEach(each -> builder
            .name("body")
            .in(ParameterType.FORMDATA)
            .contentSpecificationBuilder()
              .requestBody(true)
              .representationBuilderFor(MediaType.MULTIPART_FORM_DATA)
                .modelSpecificationBuilder()
                  .compoundModelBuilder()
                    .modelKey(new ModelKeyBuilder()
                                  .qualifiedModelName(
                                      new QualifiedModelName("io.springfox",
                                                             each.getName() + "Aggregate"))
                                  .viewDiscriminator(null)
                                  .validationGroupDiscriminators(new ArrayList<>())
                                  .isResponse(false).build())
                    .properties(properties(each))
                    .yield()
                  .yield(RepresentationBuilder.class)
                .encodingForProperty(each.getName())
                .copyOf(encoding(each, MediaType.MULTIPART_FORM_DATA))
                .yield()
              .yield()
            .yield()
            .build());
    RequestParameter content = builder.build();
    // @formatter:on

    ArrayList<RequestParameter> requestParameters =
        parameters.stream()
                  .filter(p -> p.getIn() != ParameterType.FORMDATA
                      && p.getIn() != ParameterType.FORM)
                  .collect(Collectors.toCollection(ArrayList::new));
    requestParameters.add(content);
    return requestParameters;
  }

  private List<PropertySpecification> properties(RequestParameter parameter) {
    return parameter.getParameterSpecification().getContent()
                    .map(c -> c.getRepresentations().stream()
                               .map(m -> new PropertySpecificationBuilder(
                                   parameter.getName(),
                                   null)
                                   .type(m.getModel())
                                   .build())
                               .collect(Collectors.toList()))
                    .orElse(new ArrayList<>());
  }

  private Encoding encoding(
      RequestParameter parameter,
      MediaType mediaType) {
    return parameter.getParameterSpecification()
                    .getContent()
                    .flatMap(c -> c.representationFor(mediaType)
                                   .flatMap(r -> r.getEncodings().stream()
                                                  .filter(e -> parameter.getName().equals(e.getPropertyRef()))
                                                  .findFirst()))
                    .orElse(null);
  }


}
