package springfox.documentation.builders;

import org.slf4j.Logger;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarModelSpecification;
import springfox.documentation.schema.ScalarType;

import java.util.Arrays;
import java.util.Objects;

import static org.slf4j.LoggerFactory.*;

public class ModelSpecificationBuilder {
  private static final Logger LOGGER = getLogger(ModelSpecificationBuilder.class);
  private final Object parent;
  private final ModelFacetsBuilder facetsBuilder = new ModelFacetsBuilder(this);
  private String name;
  private ScalarModelSpecification scalar;
  private CollectionSpecification collection;
  private MapSpecification map;
  private ReferenceModelSpecification reference;
  private CompoundModelSpecificationBuilder compoundModelBuilder;

  public ModelSpecificationBuilder() {
    this(null);
  }

  public ModelSpecificationBuilder(
      Object parent) {
    this.parent = parent;
  }

  public ModelSpecificationBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ModelFacetsBuilder facetsBuilder() {
    return facetsBuilder;
  }

  public ModelSpecificationBuilder scalarModel(ScalarType type) {
    if (type != null) {
      this.scalar = new ScalarModelSpecification(type);
    }
    return this;
  }

  public ModelSpecificationBuilder scalarModel(ScalarModelSpecification scalarModelSpecification) {
    if (scalarModelSpecification != null) {
      this.scalar = scalarModelSpecification;
    }
    return this;
  }

  public CompoundModelSpecificationBuilder compoundModelBuilder() {
    if (compoundModelBuilder == null) {
      this.compoundModelBuilder = new CompoundModelSpecificationBuilder(this);
    }
    return compoundModelBuilder;
  }

  public ModelSpecificationBuilder collectionModel(CollectionSpecification collection) {
    this.collection = collection;
    return this;
  }

  public ModelSpecificationBuilder mapModel(MapSpecification map) {
    this.map = map;
    return this;
  }

  public ModelSpecificationBuilder referenceModel(ReferenceModelSpecification reference) {
    this.reference = reference;
    return this;
  }

  public ModelSpecification build() {
    CompoundModelSpecification compoundModel =
        compoundModelBuilder != null ? compoundModelBuilder.build() : null;
    ensureValidSpecification(
        scalar,
        compoundModel,
        reference,
        collection,
        map);
    return new ModelSpecification(
        name,
        facetsBuilder.build(),
        scalar,
        compoundModel,
        collection,
        map,
        reference);
  }

  public ModelSpecificationBuilder copyOf(ModelSpecification other) {
    if (other != null) {
      ScalarModelSpecification scalar = other.getScalar().orElse(null);
      ReferenceModelSpecification reference = other.getReference().orElse(null);
      CompoundModelSpecification compound = other.getCompound().orElse(null);
      CollectionSpecification collection = other.getCollection().orElse(null);
      MapSpecification map = other.getMap().orElse(null);

      this.scalar = null;
      this.map = null;
      this.collection = null;
      this.reference = null;
      this.compoundModelBuilder = new CompoundModelSpecificationBuilder(this);

      this.name(other.getName())
          .scalarModel(scalar)
          .referenceModel(reference)
          .compoundModelBuilder()
          .copyOf(compound)
          .yield()
          .collectionModel(collection)
          .mapModel(map)
          .facetsBuilder()
          .copyOf(other.getFacets());
    }
    return this;
  }

  private void ensureValidSpecification(
      Object... specs) {
    long specCount = Arrays.stream(specs)
        .filter(Objects::nonNull)
        .count();
    if (specCount == 0) {
      LOGGER.error(
          "Error building model {}",
          name);
      throw new IllegalArgumentException("At least one type of specification is required");
    }
    if (specCount > 1) {
      LOGGER.error(
          "Error building model {}",
          name);
      throw new IllegalArgumentException("Only one of the specifications should be non null");
    }
  }

  public <T> T yield(Class<T> clazz) {
    return (T) parent;
  }
}