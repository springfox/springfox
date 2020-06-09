package springfox.documentation.swagger2.mappers;

import io.swagger.models.Model;
import io.swagger.models.parameters.AbstractSerializableParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.CookieParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.ContentSpecification;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Representation;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;

@Mapper(componentModel = "spring")
public class RequestParameterMapper {
  private static final Set<String> SUPPORTED_FORM_DATA_TYPES = Stream.of(
      "string",
      "number",
      "integer",
      "boolean",
      "array",
      "file").collect(toSet());

  private static final VendorExtensionsMapper VENDOR_EXTENSIONS_MAPPER = new VendorExtensionsMapper();
  private final PropertyMapper propertyMapper = Mappers.getMapper(PropertyMapper.class);

  @SuppressWarnings({ "CyclomaticComplexity", "JavaNCSS", "NestedIfDepth" })
  Optional<Parameter> mapParameter(
      RequestParameter from,
      @Context ModelNamesRegistry modelNamesRegistry) {
    Parameter parameter;
    ParameterSpecification parameterSpecification = from.getParameterSpecification();
    Optional<SimpleParameterSpecification> query = parameterSpecification.getQuery();
    if (query.isPresent()) {
      switch (from.getIn()) {
        case COOKIE:
          parameter = new CookieParameter();
          break;
        case HEADER:
          parameter = new HeaderParameter();
          break;
        case PATH:
          parameter = new PathParameter();
          break;
        case QUERY:
        default:
          parameter = new QueryParameter();
          break;
      }
      SimpleParameterSpecification paramSpecification = query.get();
      ((AbstractSerializableParameter) parameter).setDefaultValue(
          paramSpecification.getDefaultValue());
      ((AbstractSerializableParameter) parameter)
          .setAllowEmptyValue(query.map(SimpleParameterSpecification::getAllowEmptyValue).orElse(null));
      parameter.setName(from.getName());
      parameter.setDescription(from.getDescription());
      Parameter finalParameter = parameter;
      paramSpecification.facetOfType(StringElementFacet.class)
          .ifPresent(sf -> finalParameter.setPattern(sf.getPattern()));
      parameter.setRequired(from.getRequired());
      parameter.getVendorExtensions()
          .putAll(VENDOR_EXTENSIONS_MAPPER.mapExtensions(from.getExtensions()));
      query.ifPresent(q -> maybeAddAllowableValuesToParameter((AbstractSerializableParameter) finalParameter, q));
      if (paramSpecification.getModel() == null) {
        //TODO: why this condition? ^^^^^^^
        ((AbstractSerializableParameter) parameter).setType(ScalarType.STRING.getType());
      } else {
        Optional<CollectionSpecification> collection = paramSpecification.getModel().getCollection();
        if (collection.isPresent()) {
          if (collection.get()
              .getModel().getScalar()
              .map(ScalarModelSpecification::getType)
              .orElse(null) == ScalarType.BYTE) {
            ((AbstractSerializableParameter) parameter).setType(ScalarType.BYTE.getType());
            ((AbstractSerializableParameter) parameter).setFormat(ScalarType.BYTE.getFormat());
          } else {
            ((AbstractSerializableParameter) parameter).setCollectionFormat(query.get().getCollectionFormat()
                .getType());
            ((AbstractSerializableParameter) parameter).setType("array");
            paramSpecification.getModel().getCollection()
                .map(CollectionSpecification::getModel)
                .ifPresent(model -> {
                  Property property = propertyMapper.fromModel(model, modelNamesRegistry);
                  Property itemProperty
                      = maybeAddFacets(
                      property,
                      model.getFacets());
                  ((AbstractSerializableParameter) finalParameter).setItems(itemProperty);
                  maybeAddFacets(itemProperty, model.getFacets());
                });

          }
        } else if (paramSpecification.getModel().getMap().isPresent()) {
          ModelSpecification value = paramSpecification.getModel().getMap().get().getValue();
          Property itemProperty = propertyMapper.fromModel(value, modelNamesRegistry);
          Property mapProperty = new MapProperty(itemProperty);
          ((AbstractSerializableParameter) parameter).setItems(mapProperty);
        } else {
          ((AbstractSerializableParameter) parameter).setDefaultValue(paramSpecification.getDefaultValue());
          if (from.getScalarExample() != null) {
            ((AbstractSerializableParameter) parameter).setExample(String.valueOf(from.getScalarExample()));
          }
          query.map(SimpleParameterSpecification::getModel)
              .flatMap(ModelSpecification::getScalar)
              .ifPresent(scalar -> {
                ((AbstractSerializableParameter) finalParameter).setType(scalar.getType().getType());
                ((AbstractSerializableParameter) finalParameter).setFormat(scalar.getType().getFormat());
              });

        }
      }
    } else if (from.getIn() == ParameterType.FORMDATA) {
      parameter = formParameter(from, modelNamesRegistry);
    } else {
      parameter = bodyParameter(from, modelNamesRegistry);
    }
    return Optional.ofNullable(parameter);
  }

  private Parameter formParameter(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {

    FormParameter parameter = new FormParameter()
        .name(source.getName())
        .description(source.getDescription());
    Optional<ContentSpecification> content = source.getParameterSpecification().getContent();
    content.get().getRepresentations().stream()
        .findFirst()
        .map(Representation::getModel)
        .ifPresent(m -> parameter.setProperty(propertyMapper.fromModel(m, namesRegistry)));

    if (!SUPPORTED_FORM_DATA_TYPES.contains(parameter.getType())
        || "array".equals(parameter.getType())
        && !SUPPORTED_FORM_DATA_TYPES.contains(parameter.getItems().getType())) {
      // Falling back to BodyParameter is non-compliant with the Swagger 2.0 spec,
      // but matches previous behavior.
      return bodyParameter(source, namesRegistry);
    }

    parameter.setIn(source.getIn().getIn());
    Optional<Representation> urlEncoded = content.get().getRepresentations().stream()
        .filter(r -> r.getMediaType() == MediaType.APPLICATION_FORM_URLENCODED)
        .findFirst();
    urlEncoded.flatMap(m -> m.getModel().getFacets().elementFacet(StringElementFacet.class))
        .ifPresent(sf -> parameter.setPattern(sf.getPattern()));
    parameter.setRequired(source.getRequired());
    parameter.getVendorExtensions().putAll(VENDOR_EXTENSIONS_MAPPER.mapExtensions(source.getExtensions()));
    if (source.getScalarExample() != null) {
      parameter.setExample(String.valueOf(source.getScalarExample()));
    }
    for (Example example : source.getExamples()) {
      if (example.getValue() != null) {
        // Form parameters only support a single example
        parameter.example(String.valueOf(example.getValue()));
        break;
      }
    }
    return parameter;
  }

  private Parameter bodyParameter(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {
    Model schema = toSchema(source, namesRegistry);
    if (source.getScalarExample() != null) {
      schema.setExample(
          String.valueOf(source.getScalarExample().getValue()));
    }
    BodyParameter parameter = new BodyParameter()
        .description(source.getDescription())
        .name(source.getName())
        .schema(schema);
    parameter.setIn(source.getIn().getIn());
    parameter.setRequired(source.getRequired());
    parameter.getVendorExtensions().putAll(VENDOR_EXTENSIONS_MAPPER.mapExtensions(source.getExtensions()));
    for (Example example : source.getExamples()) {
      if (example.getValue() != null) {
        // Form parameters only support a single example
        parameter.example(example.getMediaType().orElse("default"), String.valueOf(example.getValue()));
        break;
      }
    }
    //TODO: swagger-core Body parameter does not have an enum property
    return parameter;
  }

  private Model toSchema(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {
    return source.getParameterSpecification().getContent()
        .map(c -> Mappers.getMapper(ModelSpecificationMapper.class)
            .mapModels(c.getRepresentations().first().getModel(), namesRegistry))
        .orElse(null);
  }
}