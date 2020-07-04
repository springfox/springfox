package springfox.documentation.spring.web.readers.operation;

import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.service.Encoding;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.*;

@Component
public class ContentParameterAggregator implements ParameterAggregator {
  private static final Logger LOGGER = getLogger(ContentParameterAggregator.class);

  @SuppressWarnings("CyclomaticComplexity")
  public Collection<RequestParameter> aggregate(Collection<RequestParameter> parameters) {
    if (parameters.size() == 0) {
      return new ArrayList<>();
    }
    LOGGER.debug(
        "Aggregating content parameters from parameters: {}",
        parameters.stream()
            .map(RequestParameter::getName)
            .collect(Collectors.joining(", ")));

    MediaType contentMediaType = MediaType.MULTIPART_FORM_DATA;
    ParameterType in = ParameterType.FORMDATA;
    if (parameters.stream().noneMatch(p -> p.getIn() == ParameterType.FORMDATA)) {
      contentMediaType = MediaType.APPLICATION_FORM_URLENCODED;
      in = ParameterType.FORM;
    }
    final MediaType aggregateMediaType = contentMediaType;
    final ParameterType aggregateIn = in;
    RequestParameterBuilder builder = new RequestParameterBuilder();

    parameters.stream()
        .filter(p -> p.getIn() == ParameterType.FORM)
        .forEach(each -> builder
            .name("body")
            .in(aggregateIn)
            .content(q -> q
                .requestBody(true)
                .representation(aggregateMediaType)
                .apply(r -> r.model(m -> m.compoundModel(cm -> cm
                    .modelKey(mk ->
                        mk.qualifiedModelName(qn ->
                            qn.namespace("io.springfox")
                                .name(each.getName() + "Aggregate"))
                            .viewDiscriminator(null)
                            .validationGroupDiscriminators(new ArrayList<>())
                            .isResponse(false)
                            .build())
                    .properties(properties(each))))
                    .encoding(each.getName()).apply(e -> e.copyOf(encoding(each, MediaType.TEXT_PLAIN)))
                    .build())));

    parameters.stream()
        .filter(p -> p.getIn() == ParameterType.FORMDATA
            && p.getParameterSpecification().getContent()
            .map(c -> c.getRepresentations().stream()
                .anyMatch(m -> m.getMediaType().equals(MediaType.MULTIPART_FORM_DATA)
                    || m.getMediaType().equals(MediaType.MULTIPART_MIXED)
                    || m.getMediaType().equals(MediaType.APPLICATION_OCTET_STREAM)
                    || m.getMediaType().equals(MediaType.ALL)))
            .orElse(false))
        .forEach(each -> builder
            .name("body")
            .in(aggregateIn)
            .content(c -> c
                .requestBody(true)
                .representation(aggregateMediaType)
                .apply(r -> r.model(m ->
                    m.compoundModel(cm ->
                        cm.modelKey(mk ->
                            mk.qualifiedModelName(q ->
                                q.namespace("io.springfox")
                                    .name(each.getName() + "Aggregate"))
                                .viewDiscriminator(null)
                                .validationGroupDiscriminators(new ArrayList<>())
                                .isResponse(false).build())
                            .properties(properties(each))))
                    .encoding(each.getName()).apply(e -> e.copyOf(encoding(each, aggregateMediaType)))))
            .build());
    RequestParameter content = builder.build();

    ArrayList<RequestParameter> requestParameters =
        parameters.stream()
            .filter(p -> p.getIn() != ParameterType.FORMDATA
                && p.getIn() != ParameterType.FORM)
            .collect(Collectors.toCollection(ArrayList::new));
    if (content != null && content.getIn() != null) {
      requestParameters.add(content);
    }
    LOGGER.debug(
        "Post content aggregation parameters: {}",
        requestParameters.stream()
            .map(RequestParameter::getName)
            .collect(Collectors.joining(", ")));
    return requestParameters;
  }

  private List<PropertySpecification> properties(RequestParameter parameter) {
    return parameter.getParameterSpecification().getContent()
        .map(c -> c.getRepresentations().stream()
            .map(m -> new PropertySpecificationBuilder(parameter.getName())
                .type(m.getModel())
                .required(parameter.getRequired())
                .description(parameter.getDescription())
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
