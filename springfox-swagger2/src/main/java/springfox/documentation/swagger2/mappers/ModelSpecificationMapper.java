package springfox.documentation.swagger2.mappers;

import io.swagger.models.ArrayModel;
import io.swagger.models.ComposedModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.RefModel;
import io.swagger.models.Xml;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.Property;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import springfox.documentation.schema.CollectionElementFacet;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.NumericElementFacet;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.schema.StringElementFacet;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ModelNamesRegistry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static springfox.documentation.builders.BuilderDefaults.*;

@Mapper
public abstract class ModelSpecificationMapper {
  private final PropertyMapper propertyMapper = new PropertyMapper();

  public Map<String, Model> modelsFromApiListings(Map<String, List<ApiListing>> apiListings) {
    Map<String, Model> modelMap = new TreeMap<>();
    apiListings.values()
        .forEach(listings -> listings
            .forEach(each -> modelMap.putAll(
                mapModels(each.getModelSpecifications(), each.getModelNamesRegistry()))));
    return modelMap;
  }

  protected abstract Map<String, Model> mapModels(
      Map<String, ModelSpecification> specifications,
      @Context ModelNamesRegistry modelNamesRegistry);

  public Model mapModels(
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

  @SuppressWarnings({"CyclomaticComplexity", "NPathComplexity", "JavaNCSS", "unchecked"})
  private Model model(
      ModelSpecification source,
      @Context ModelNamesRegistry namesRegistry) {
    Model toReturn;
    ModelImpl model = new ModelImpl()
        .description(source.getFacets().map(ModelFacets::getDescription).orElse(null))
        .discriminator(source.getCompound()
            .map(CompoundModelSpecification::getDiscriminator)
            .orElse(null))
        .example(source.getFacets().map(ModelFacets::getExamples).orElse(EMPTY_LIST).stream()
            .findFirst().orElse(null))
        .name(source.getName())
        .xml(mapXml(source.getFacets().map(ModelFacets::getXml).orElse(null)));

    toReturn = source.getCompound()
        .map(c -> {
          Map<String, Property> modelProperties = new TreeMap<>(Comparator.naturalOrder());
          Map<String, PropertySpecification> properties = c.getProperties().stream()
              .collect(toMap(PropertySpecification::getName, Function.identity()));
          modelProperties.putAll(mapProperties(properties, namesRegistry));
          model.setProperties(modelProperties);
          List<String> requiredFields = properties.values().stream()
              .filter(PropertySpecification::nullSafeIsRequired)
              .map(PropertySpecification::getName)
              .filter(Objects::nonNull)
              .collect(toList());
          model.setRequired(requiredFields);
          model.setSimple(false);
          model.setType(ModelImpl.OBJECT);
          model.setTitle(source.getFacets().map(ModelFacets::getTitle).orElse(null));
          return model;
        })
        .orElse(null);
    if (toReturn != null) {
      return toReturn;
    }

    toReturn = source.getScalar()
        .map(s -> {
          model.setType(s.getType().getType());
          model.setFormat(s.getType().getFormat());
          source.getFacets().flatMap(f -> f.elementFacet(EnumerationFacet.class))
              .ifPresent(ef -> model._enum(ef.getAllowedValues()));
          source.getFacets().flatMap(f -> f.elementFacet(StringElementFacet.class))
              .ifPresent(sf -> {
                model.setPattern(sf.getPattern());
                model.setMinLength(sf.getMinLength());
                model.setMaxLength(sf.getMaxLength());
              });
          source.getFacets().flatMap(f -> f.elementFacet(NumericElementFacet.class))
              .ifPresent(nf -> {
                model.maximum(nf.getMaximum());
                model.minimum(nf.getMinimum());
                model.setExclusiveMaximum(nf.getExclusiveMaximum());
                model.setExclusiveMinimum(nf.getExclusiveMinimum());
              });
          return model;
        })
        .orElse(null);
    if (toReturn != null) {
      return toReturn;
    }

    toReturn = source.getReference()
        .map(r -> {
          if (emptyToNull(r.getKey().getQualifiedModelName().getName()) != null) { //TODO: Find out why
            RefModel refModel = new RefModel();
            refModel.set$ref(namesRegistry.nameByKey(r.getKey())
                .orElse("ERROR - " + r.getKey().getQualifiedModelName()));
            return refModel;
          }
          return null;
        })
        .orElse(null);
    if (toReturn != null) {
      return toReturn;
    }
    toReturn = source.getCollection()
        .map(c -> {
          ModelSpecification itemSpec = c.getModel();
          ArrayModel arrayModel = new ArrayModel()
              .description(source.getFacets().map(ModelFacets::getDescription).orElse(null));
          arrayModel.setExample(source.getFacets().map(ModelFacets::getExamples).orElse(EMPTY_LIST).stream()
              .findFirst()
              .orElse(null));
          if (itemSpec.getScalar().isPresent()) {
            arrayModel.items(
                new ScalarModelToPropertyConverter()
                    .convert(itemSpec.getScalar().get()));
          } else if (itemSpec.getCompound().isPresent()) {
            arrayModel.items(
                new CompoundSpecificationToPropertyConverter(namesRegistry)
                    .convert(itemSpec.getCompound().get()));
          } else if (itemSpec.getCollection().isPresent()) {
            arrayModel.items(
                new CollectionSpecificationToPropertyConverter(namesRegistry)
                    .convert(itemSpec.getCollection().get()));
          } else if (itemSpec.getReference().isPresent()) {
            arrayModel.items(
                new ReferenceModelSpecificationToPropertyConverter(namesRegistry)
                    .convert(itemSpec.getReference().get()));
          }
          source.getFacets().flatMap(f -> f.elementFacet(CollectionElementFacet.class))
              .ifPresent(cf -> {
                arrayModel.setMaxItems(cf.getMaxItems());
                arrayModel.setMinItems(cf.getMinItems());
                arrayModel.setUniqueItems(cf.getUniqueItems());
              });
          return arrayModel;
        })
        .orElse(null);
    if (toReturn != null) {
      return toReturn;
    }

    return source.getMap()
        .map(map -> {
          ModelSpecification valueSpec = map.getValue();
          if (valueSpec.getScalar().isPresent()) {
            model.additionalProperties(
                new ScalarModelToPropertyConverter()
                    .convert(valueSpec.getScalar().get()));
          } else if (valueSpec.getCompound().isPresent()) {
            model.additionalProperties(
                new CompoundSpecificationToPropertyConverter(namesRegistry)
                    .convert(valueSpec.getCompound().get()));
          } else if (valueSpec.getCollection().isPresent()) {
            model.additionalProperties(
                new CollectionSpecificationToPropertyConverter(namesRegistry)
                    .convert(valueSpec.getCollection().get()));
          } else if (valueSpec.getReference().isPresent()) {
            model.additionalProperties(
                new ReferenceModelSpecificationToPropertyConverter(namesRegistry)
                    .convert(valueSpec.getReference().get()));
          } else {
            model.additionalProperties(new ObjectProperty());
          }
          return model;
        })
        .orElse(null);
  }

  @SuppressWarnings("unchecked")
  private Model mapComposedModel(
      RefModel parent,
      ModelSpecification source,
      ModelNamesRegistry namesRegistry) {
    ComposedModel model = new ComposedModel()
        .interfaces(singletonList(parent))
        .child(model(source, namesRegistry));

    model.setDescription(source.getFacets().map(ModelFacets::getDescription).orElse(null));
    model.setExample(source.getFacets().map(ModelFacets::getExamples).orElse(EMPTY_LIST)
        .stream().findFirst().orElse(null));
    model.setTitle(source.getName());

    Map<String, PropertySpecification> properties = source.getCompound()
        .map(c -> c.getProperties().stream()
            .collect(Collectors.toMap(PropertySpecification::getName, Function.identity())))
        .orElse(new HashMap<>());
    Map<String, Property> modelProperties = new TreeMap<>(Comparator.naturalOrder());
    modelProperties.putAll(mapProperties(properties, namesRegistry));
    model.setProperties(modelProperties);
    return model;
  }

  protected Map<String, Property> mapProperties(
      Map<String, PropertySpecification> properties,
      ModelNamesRegistry modelNamesRegistry) {
    return properties.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(
            Comparator.comparing(PropertySpecification::getPosition)
                .thenComparing(PropertySpecification::getName)))
        .collect(toMap(
            Map.Entry::getKey,
            e -> propertyMapper.fromProperty(e.getValue(), modelNamesRegistry),
            (p1, p2) -> p1,
            TreeMap::new));
  }

  private Xml mapXml(springfox.documentation.schema.Xml xml) {
    if (xml == null) {
      return null;
    }
    return new Xml()
        .name(xml.getName())
        .attribute(xml.getAttribute())
        .namespace(xml.getNamespace())
        .prefix(xml.getPrefix())
        .wrapped(xml.getWrapped());
  }
}
