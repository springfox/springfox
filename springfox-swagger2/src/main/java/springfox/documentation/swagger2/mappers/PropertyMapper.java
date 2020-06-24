package springfox.documentation.swagger2.mappers;

import io.swagger.models.Xml;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.StringProperty;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import springfox.documentation.schema.ModelFacets;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.PropertySpecification;
import springfox.documentation.service.ModelNamesRegistry;

import java.util.Map;

import static springfox.documentation.swagger2.mappers.EnumMapper.*;

@Mapper
public class PropertyMapper {

  @SuppressWarnings("NPathComplexity")
  public Property fromModel(
      ModelSpecification modelSpecification,
      @Context ModelNamesRegistry modelNamesRegistry) {
    if (modelSpecification == null) {
      return null;
    }
    Property property;
    property = modelSpecification.getScalar()
        .map(sm -> new ScalarModelToPropertyConverter().convert(sm))
        .orElse(null);

    if (property == null) {
      property = modelSpecification.getCompound()
          .map(cm -> new CompoundSpecificationToPropertyConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (property == null) {
      property = modelSpecification.getMap()
          .map(mm -> new MapSpecificationToPropertyConverter(modelNamesRegistry).convert(mm))
          .orElse(null);
    }

    if (property == null) {
      property = modelSpecification.getCollection()
          .map(cm -> new CollectionSpecificationToPropertyConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (property == null) {
      property = modelSpecification.getReference()
          .map(cm -> new ReferenceModelSpecificationToPropertyConverter(modelNamesRegistry).convert(cm))
          .orElse(null);
    }

    if (property != null) {
      property.setName(modelSpecification.getName());
      maybeAddFacets(property, modelSpecification.getFacets().orElse(null));
    }
    
    return property;
  }

  public Property fromProperty(
      PropertySpecification source,
      @Context ModelNamesRegistry modelNamesRegistry) {
    Property property = fromModel(source.getType(), modelNamesRegistry);

    ModelFacets facets = source.getType().getFacets().orElse(null);
    maybeAddFacets(property, facets);
    maybeAddFacets(property, source);

    if (property instanceof ArrayProperty) {
      ArrayProperty arrayProperty = (ArrayProperty) property;
      maybeAddFacets(
          arrayProperty.getItems(),
          source.getType().getCollection()
              .flatMap(c -> c.getModel().getFacets())
              .orElse(null));
    }

    if (property instanceof MapProperty) {
      MapProperty mapProperty = (MapProperty) property;
      maybeAddFacets(
          mapProperty.getAdditionalProperties(),
          source.getType().getMap()
              .flatMap(c -> c.getValue().getFacets())
              .orElse(null));
    }

    if (property instanceof StringProperty) {
      StringProperty stringProperty = (StringProperty) property;
      stringProperty.setDefault(source.getDefaultValue() != null ? String.valueOf(source.getDefaultValue()) : null);
    }

    Map<String, Object> extensions = new VendorExtensionsMapper()
        .mapExtensions(source.getVendorExtensions());

    if (property != null) {
      property.setDescription(source.getDescription());
      property.setName(source.getName());
      property.setRequired(source.getRequired() == null ? false : source.getRequired());
      property.setReadOnly(source.getReadOnly());
      property.setAllowEmptyValue(source.getAllowEmptyValue());
      property.setExample(source.getExample());
      property.getVendorExtensions().putAll(extensions);
      property.setXml(mapXml(source.getXml()));
    }

    return property;
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
