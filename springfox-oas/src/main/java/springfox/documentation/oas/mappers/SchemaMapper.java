package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.XML;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.ElementFacetSource;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ModelNamesRegistry;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@SuppressWarnings("rawtypes")
@Mapper(componentModel = "spring")
public abstract class SchemaMapper {
  @Named("ModelsMapping")
  public Map<String, Schema> modelsFromApiListings(Map<String, List<ApiListing>> apiListings) {
    Map<String, Schema> modelMap = new TreeMap<>();
    apiListings.values()
               .forEach(listings -> listings
                   .forEach(each -> modelMap.putAll(
                       mapModels(each.getModelSpecifications(), each.getModelNamesRegistry()))));
    return modelMap;
  }

  protected Map<String, Schema> mapModels(
      Map<String, ModelSpecification> specifications,
      ModelNamesRegistry modelNamesRegistry) {
    if (specifications == null) {
      return null;
    }

    Map<String, Schema> map = new HashMap<>(Math.max((int) (specifications.size() / .75f) + 1, 16));

    for (java.util.Map.Entry<String, ModelSpecification> entry : specifications.entrySet()) {
      String key = entry.getKey();
      Schema value = mapModel(entry.getValue(), modelNamesRegistry);
      map.put(key, value);
    }

    return map;
  }

  @Named("ModelsMapping")
  public Schema mapModel(
      ModelSpecification source,
      @Context ModelNamesRegistry namesRegistry) {
    if (source == null) {
      return null;
    }
    ModelSpecificationInheritanceDeterminer determiner = new ModelSpecificationInheritanceDeterminer(namesRegistry);
    return determiner.parent(source)
                     .map(rm -> mapComposedModel(
                         rm,
                         source,
                         namesRegistry))
                     .orElse(model(source, namesRegistry));
  }

  @SuppressWarnings({
      "CyclomaticComplexity",
      "NPathComplexity",
      "JavaNCSS" })
  private Schema model(
      ModelSpecification source,
      @Context ModelNamesRegistry namesRegistry) {
    Schema model = mapFrom(source, namesRegistry)
        .description(source.getFacets().getDescription())
//        .discriminator(source.getCompound()
//                             .map(CompoundModelSpecification::getDiscriminator)
//                             .orElse(null)) //TODO: Some work here
        .example(source.getFacets().getExamples().stream()
                       .findFirst().orElse(null))
        .name(source.getName())
        .xml(mapXml(source.getFacets().getXml()));

    source.getCompound()
          .ifPresent(c -> {
            Map<String, Schema> modelProperties = new TreeMap<>(Comparator.naturalOrder());
            Map<String, PropertySpecification> properties = c.getProperties().stream()
                                                             .collect(toMap(
                                                                 PropertySpecification::getName,
                                                                 Function
                                                                     .identity()));
            modelProperties.putAll(mapProperties(properties, namesRegistry));
            model.setProperties(modelProperties);
            List<String> requiredFields = properties.values().stream()
                                                    .filter(PropertySpecification::nullSafeIsRequired)
                                                    .map(PropertySpecification::getName)
                                                    .filter(Objects::nonNull)
                                                    .collect(toList());
            model.setRequired(requiredFields);
            model.setType("object");
            model.setTitle(source.getFacets().getTitle());
          });


    source.getScalar()
          .ifPresent(s -> {
            model.setType(s.getType().getType());
            model.setFormat(s.getType().getFormat());
            source.getFacets().elementFacet(EnumerationFacet.class)
                  .ifPresent(ef -> model.setEnum(ef.getAllowedValues()));
            source.getFacets().elementFacet(StringElementFacet.class)
                  .ifPresent(sf -> {
                    model.setPattern(sf.getPattern());
                    model.setMinLength(sf.getMinLength());
                    model.setMaxLength(sf.getMaxLength());
                  });
            source.getFacets().elementFacet(NumericElementFacet.class)
                  .ifPresent(nf -> {
                    model.maximum(nf.getMaximum());
                    model.minimum(nf.getMinimum());
                    model.setExclusiveMaximum(nf.getExclusiveMaximum());
                    model.setExclusiveMinimum(nf.getExclusiveMinimum());
                  });
          });

    source.getReference()
          .ifPresent(r -> {
            if (emptyToNull(r.getKey().getQualifiedModelName().getName()) != null) { //TODO: Find out why
              ObjectSchema refModel = new ObjectSchema();
              refModel.type(null);
              refModel.set$ref(namesRegistry.nameByKey(r.getKey())
                                            .orElse("ERROR - " + r.getKey().getQualifiedModelName()));
            }
          });

    source.getCollection()
          .ifPresent(c -> {
            ModelSpecification itemSpec = c.getModel();
            ArraySchema arrayModel = new ArraySchema();
            arrayModel.description(source.getFacets().getDescription());
            arrayModel.setExample(source.getFacets().getExamples().stream()
                                        .findFirst()
                                        .orElse(null));
            if (itemSpec.getScalar().isPresent()) {
              arrayModel.items(
                  new ScalarModelToSchemaConverter()
                      .convert(itemSpec.getScalar().get()));
            } else if (itemSpec.getCompound().isPresent()) {
              arrayModel.items(
                  new CompoundSpecificationToSchemaConverter(namesRegistry)
                      .convert(itemSpec.getCompound().get()));
            } else if (itemSpec.getCollection().isPresent()) {
              arrayModel.items(
                  new CollectionSpecificationToSchemaConverter(namesRegistry)
                      .convert(itemSpec.getCollection().get()));
            } else if (itemSpec.getReference().isPresent()) {
              arrayModel.items(
                  new ReferenceModelSpecificationToSchemaConverter(namesRegistry)
                      .convert(itemSpec.getReference().get()));
            }
            source.getFacets().elementFacet(CollectionElementFacet.class)
                  .ifPresent(cf -> {
                    arrayModel.setMaxItems(cf.getMaxItems());
                    arrayModel.setMinItems(cf.getMinItems());
                    arrayModel.setUniqueItems(cf.getUniqueItems());
                  });
          });

    source.getMap()
          .ifPresent(map -> {
            ModelSpecification valueSpec = map.getValue();
            if (valueSpec.getScalar().isPresent()) {
              model.additionalProperties(
                  new ScalarModelToSchemaConverter()
                      .convert(valueSpec.getScalar().get()));
            } else if (valueSpec.getCompound().isPresent()) {
              model.additionalProperties(
                  new CompoundSpecificationToSchemaConverter(namesRegistry)
                      .convert(valueSpec.getCompound().get()));
            } else if (valueSpec.getCollection().isPresent()) {
              model.additionalProperties(
                  new CollectionSpecificationToSchemaConverter(namesRegistry)
                      .convert(valueSpec.getCollection().get()));
            } else if (valueSpec.getReference().isPresent()) {
              model.additionalProperties(
                  new ReferenceModelSpecificationToSchemaConverter(namesRegistry)
                      .convert(valueSpec.getReference().get()));
            } else {
              model.additionalProperties(new ObjectSchema());
            }
          });

    return model;
  }

  private Schema mapComposedModel(
      Schema parent,
      ModelSpecification source,
      ModelNamesRegistry namesRegistry) {
    ComposedSchema model = new ComposedSchema()
        .addAllOfItem(parent)
        .addAllOfItem(model(source, namesRegistry));

    model.setDescription(source.getFacets().getDescription());
    model.setExample(source.getFacets().getExamples().stream().findFirst().orElse(null));
    model.setTitle(source.getName());

    Map<String, PropertySpecification> properties = source.getCompound()
                                                          .map(c -> c.getProperties().stream()
                                                                     .collect(Collectors.toMap(
                                                                         PropertySpecification::getName,
                                                                         Function.identity())))
                                                          .orElse(new HashMap<>());
    Map<String, Schema> modelProperties = new TreeMap<>(Comparator.naturalOrder());
    modelProperties.putAll(mapProperties(properties, namesRegistry));
    model.setProperties(modelProperties);
    return model;
  }

  protected Map<String, Schema> mapProperties(
      Map<String, PropertySpecification> properties,
      ModelNamesRegistry modelNamesRegistry) {
    return properties.entrySet().stream()
                     .collect(toMap(
                         Map.Entry::getKey,
                         e -> fromProperty(e.getValue(), modelNamesRegistry)));
  }

  private Schema fromProperty(
      PropertySpecification source,
      ModelNamesRegistry modelNamesRegistry) {
    Schema property = model(source.getType(), modelNamesRegistry);

    ModelFacets facets = source.getType().getFacets();
    maybeAddFacets(property, facets);
    maybeAddFacets(property, source);

    if (property instanceof ArraySchema) {
      ArraySchema arrayProperty = (ArraySchema) property;
      maybeAddFacets(
          arrayProperty.getItems(),
          source.getType().getCollection()
                .map(c -> c.getModel().getFacets())
                .orElse(null));
    }

    if (property instanceof MapSchema) {
      MapSchema mapProperty = (MapSchema) property;
      maybeAddFacets(
          (Schema) mapProperty.getAdditionalProperties(),
          source.getType().getMap()
                .map(c -> c.getValue().getFacets())
                .orElse(null));
    }

    if (property instanceof StringSchema) {
      StringSchema stringProperty = (StringSchema) property;
      stringProperty.setDefault(source.getDefaultValue() != null ? String.valueOf(source.getDefaultValue()) : null);
    }

    Map<String, Object> extensions = new VendorExtensionsMapper()
        .mapExtensions(source.getVendorExtensions());

    if (property != null) {
      property.setDescription(source.getDescription());
      property.setName(source.getName());
      property.setReadOnly(source.getReadOnly());
//      property.setAllowEmptyValue(source.getAllowEmptyValue());
      property.setExample(source.getExample());
      property.setExtensions(extensions);
      property.setXml(mapXml(source.getXml()));
    }

    return property;
  }

  private XML mapXml(springfox.documentation.schema.Xml xml) {
    if (xml == null) {
      return null;
    }
    return new XML()
        .name(xml.getName())
        .attribute(xml.getAttribute())
        .namespace(xml.getNamespace())
        .prefix(xml.getPrefix())
        .wrapped(xml.getWrapped());
  }

  public Schema mapFrom(
      ModelSpecification modelSpecification,
      @Context ModelNamesRegistry modelNamesRegistry) {
    Schema<?> schema;
    schema = modelSpecification.getScalar()
                               .map(sm -> new ScalarModelToSchemaConverter().convert(sm))
                               .orElse(null);

    if (schema == null) {
      schema = modelSpecification.getCompound()
                                 .map(cm -> new CompoundSpecificationToSchemaConverter(modelNamesRegistry).convert(cm))
                                 .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getMap()
                                 .map(mm -> new MapSpecificationToSchemaConverter(modelNamesRegistry).convert(mm))
                                 .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getCollection()
                                 .map(cm -> new CollectionSpecificationToSchemaConverter(modelNamesRegistry)
                                     .convert(cm))
                                 .orElse(null);
    }

    if (schema == null) {
      schema = modelSpecification.getReference()
                                 .filter(r -> emptyToNull(r.getKey().getQualifiedModelName().getName()) != null)
                                 .map(cm -> new ReferenceModelSpecificationToSchemaConverter(modelNamesRegistry)
                                     .convert(cm))
                                 .orElse(null);
    }

    if (schema != null) {
      schema.setName(modelSpecification.getName());
    }

    return schema;
  }


  @SuppressWarnings({
      "NPathComplexity",
      "CyclomaticComplexity" })
  static Schema maybeAddFacets(
      Schema property,
      ElementFacetSource facets) {
    if (facets == null) {
      return property;
    }
    facets.elementFacet(EnumerationFacet.class).ifPresent(f -> {
      if (property instanceof StringSchema) {
        StringSchema stringProperty = (StringSchema) property;
        stringProperty.setEnum(f.getAllowedValues());
      } else if (property instanceof IntegerSchema) {
        IntegerSchema integerProperty = (IntegerSchema) property;
        List<? extends Number> convert = convert(f.getAllowedValues(), BigDecimal.class);
        integerProperty.setEnum((List<Number>) convert);
      } else if (property instanceof NumberSchema) {
        NumberSchema longProperty = (NumberSchema) property;
        longProperty.setEnum(convert(f.getAllowedValues(), BigDecimal.class));
      }
    });
    if (property instanceof NumberSchema) {
      facets.elementFacet(NumericElementFacet.class).ifPresent(f -> {
        NumberSchema numeric = (NumberSchema) property;
        numeric.setMaximum(f.getMaximum());
        numeric.exclusiveMaximum(f.getExclusiveMaximum());
        numeric.setMinimum(f.getMinimum());
        numeric.exclusiveMinimum(f.getExclusiveMinimum());
      });
    }
    if (property instanceof ArraySchema) {
      facets.elementFacet(CollectionElementFacet.class).ifPresent(f -> {
        ArraySchema arrayProperty = (ArraySchema) property;
        arrayProperty.setMinItems(f.getMinItems());
        arrayProperty.setMaxItems(f.getMaxItems());
      });
    }
    if (property instanceof StringSchema) {
      StringSchema stringProperty = (StringSchema) property;
      facets.elementFacet(StringElementFacet.class).ifPresent(f -> {
        stringProperty.maxLength(f.getMaxLength());
        stringProperty.minLength(f.getMinLength());
        if (f.getPattern() != null) {
          stringProperty.pattern(f.getPattern());
        }
      });
    }
    return property;
  }

  private static <T extends Number> List<T> convert(
      List<String> values,
      Class<T> toType) {
    return values.stream().map(converterOfType(toType))
                 .filter(Optional::isPresent).map(Optional::get)
                 .collect(toList());
  }

  @SuppressWarnings("unchecked")
  private static <T extends Number> Function<? super String, Optional<T>> converterOfType(final Class<T> toType) {
    return (Function<String, Optional<T>>) input -> {
      try {
        if (Integer.class.equals(toType)) {
          return (Optional<T>) of(Integer.valueOf(input));
        } else if (Long.class.equals(toType)) {
          return (Optional<T>) of(Long.valueOf(input));
        } else if (Double.class.equals(toType)) {
          return (Optional<T>) of(Double.valueOf(input));
        } else if (Float.class.equals(toType)) {
          return (Optional<T>) of(Float.valueOf(input));
        } else if (BigDecimal.class.equals(toType)) {
          return (Optional<T>) of(new BigDecimal(input));
        }
      } catch (NumberFormatException ignored) {
      }
      return empty();
    };
  }

}
