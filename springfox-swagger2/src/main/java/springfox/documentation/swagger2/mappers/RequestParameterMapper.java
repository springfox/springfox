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
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.ModelNamesRegistry;
import springfox.documentation.service.ParameterSpecification;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Representation;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.SimpleParameterSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static springfox.documentation.swagger2.mappers.EnumMapper.*;

@Mapper(componentModel = "spring")
public class RequestParameterMapper {
  private static final VendorExtensionsMapper VENDOR_EXTENSIONS_MAPPER = new VendorExtensionsMapper();
  private final PropertyMapper propertyMapper = Mappers.getMapper(PropertyMapper.class);

  @SuppressWarnings({"CyclomaticComplexity", "JavaNCSS", "NestedIfDepth"})
  Collection<Parameter> mapParameter(
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
                      model.getFacets().orElse(null));
                  ((AbstractSerializableParameter) finalParameter).setItems(itemProperty);
                  maybeAddFacets(itemProperty, model.getFacets().orElse(null));
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
                Property property = new ScalarModelToPropertyConverter().convert(scalar);
                ((AbstractSerializableParameter) finalParameter).setType(property.getType());
                ((AbstractSerializableParameter) finalParameter).setFormat(property.getFormat());
              });

        }
      }
    } else if (from.getIn() == ParameterType.FORMDATA) {
      boolean isMultipartForm = from.getParameterSpecification().getContent().map(c -> c.getRepresentations().stream()
          .anyMatch(r -> r.getMediaType().equals(MediaType.MULTIPART_MIXED)
              || r.getMediaType().equals(MediaType.MULTIPART_FORM_DATA)))
          .orElse(false);
      if (isMultipartForm) {
        return multipartFormParameters(from, modelNamesRegistry);
      }
      return formParameter(from, modelNamesRegistry);
    } else {
      boolean isUrlEncodedForm = from.getParameterSpecification().getContent().map(c -> c.getRepresentations().stream()
          .anyMatch(r -> r.getMediaType().equals(MediaType.APPLICATION_FORM_URLENCODED)))
          .orElse(false);
      if (isUrlEncodedForm) {
        return formParameter(from, modelNamesRegistry);
      }
      parameter = bodyParameter(from, modelNamesRegistry);
    }
    return Collections.singleton(parameter);
  }

  private Collection<Parameter> multipartFormParameters(
      RequestParameter from,
      ModelNamesRegistry namesRegistry) {
    return from.getParameterSpecification().getContent()
        .map(c -> c.getRepresentations().stream()
            .filter(r -> r.getMediaType().equals(MediaType.MULTIPART_MIXED)
                || r.getMediaType().equals(MediaType.MULTIPART_FORM_DATA))
            .flatMap(r -> r.getModel().getCompound()
                .map(CompoundModelSpecification::getProperties)
                .orElse(new ArrayList<>()).stream())
            .filter(prop -> prop.getType().getScalar().isPresent() ||
                prop.getType().getCollection().isPresent() &&
                    prop.getType().getCollection().get().getModel().getScalar().isPresent())
            .map(prop -> {
              FormParameter param = new FormParameter()
                  .name(prop.getName());
              prop.getType().getScalar().ifPresent(scalar ->
                  param.property(new ScalarModelToPropertyConverter().convert(scalar))
              );
              prop.getType().getFacets()
                  .flatMap(mf -> mf.elementFacet(StringElementFacet.class))
                  .ifPresent(sf -> param.setPattern(sf.getPattern()));

              prop.getType().getCollection().ifPresent(collection -> {
                Property collectionProperty = new CollectionSpecificationToPropertyConverter(namesRegistry)
                    .convert(collection);
                param.property(collectionProperty);
              });
              param.setAllowEmptyValue(prop.getAllowEmptyValue());
              param.setDefault(prop.getDefaultValue());
              param.setIn("formData");
              param.setRequired(from.getRequired());
              param.getVendorExtensions().putAll(VENDOR_EXTENSIONS_MAPPER.mapExtensions(from.getExtensions()));
              if (from.getScalarExample() != null) {
                param.setExample(String.valueOf(from.getScalarExample()));
              }
              for (Example example : from.getExamples()) {
                if (example.getValue() != null) {
                  // Form parameters only support a single example
                  param.example(String.valueOf(example.getValue()));
                  break;
                }
              }
              return param;
            }))
        .orElse(Stream.of())
        .collect(toList());
  }

  private List<Parameter> formParameter(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {
    return source.getParameterSpecification().getContent()
        .map(c -> c.getRepresentations().stream()
            .filter(r -> r.getMediaType().equals(MediaType.APPLICATION_FORM_URLENCODED))
            .flatMap(r -> r.getModel().getCompound()
                .map(CompoundModelSpecification::getProperties)
                .orElse(new ArrayList<>()).stream())
            .filter(prop -> prop.getType().getScalar().isPresent() ||
                prop.getType().getCollection().isPresent() &&
                    prop.getType().getCollection().get().getModel().getScalar().isPresent())
            .map(prop -> {
              FormParameter param = new FormParameter()
                  .description(source.getDescription())
                  .name(prop.getName());
              prop.getType().getScalar()
                  .ifPresent(scalar -> param.property(new ScalarModelToPropertyConverter().convert(scalar)));
              prop.getType().getFacets()
                  .flatMap(mf -> mf.elementFacet(StringElementFacet.class))
                  .ifPresent(sf -> param.setPattern(sf.getPattern()));

              prop.getType().getCollection().ifPresent(collection -> {
                Property collectionProperty = new CollectionSpecificationToPropertyConverter(namesRegistry)
                    .convert(collection);
                param.property(collectionProperty);
              });
              param.setAllowEmptyValue(prop.getAllowEmptyValue());
              param.setDefault(prop.getDefaultValue());
              param.setIn("formData");
              param.setRequired(source.getRequired());
              param.getVendorExtensions().putAll(VENDOR_EXTENSIONS_MAPPER.mapExtensions(source.getExtensions()));
              if (source.getScalarExample() != null) {
                param.setExample(String.valueOf(source.getScalarExample()));
              }
              for (Example example : source.getExamples()) {
                if (example.getValue() != null) {
                  // Form parameters only support a single example
                  param.example(String.valueOf(example.getValue()));
                  break;
                }
              }
              return param;
            }))
        .orElse(Stream.of())
        .collect(toList());
  }

  private Parameter bodyParameter(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {
    Model schema = toSchema(source, namesRegistry);
    if (schema != null
        && source.getScalarExample() != null) {
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
    return parameter;
  }

  private Model toSchema(
      RequestParameter source,
      ModelNamesRegistry namesRegistry) {
    return source.getParameterSpecification().getContent()
        .map(c -> Mappers.getMapper(ModelSpecificationMapper.class)
            .mapModels(
                c.getRepresentations().stream().findFirst()
                    .map(Representation::getModel)
                    .orElse(null),
                namesRegistry))
        .orElse(null);
  }
}