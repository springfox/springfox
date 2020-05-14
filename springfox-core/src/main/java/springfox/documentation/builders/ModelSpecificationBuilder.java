package springfox.documentation.builders;

import org.slf4j.Logger;
import springfox.documentation.schema.CollectionSpecification;
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
    ensureValidSpecification(
        scalar,
        compoundModelBuilder != null ? compoundModelBuilder.build() : null,
        reference,
        collection,
        map);
    return new ModelSpecification(
        name,
        facetsBuilder.build(),
        scalar,
        compoundModelBuilder != null ? compoundModelBuilder.build() : null,
        collection,
        map,
        reference);
  }

  public ModelSpecificationBuilder copyOf(ModelSpecification other) {
    if (other != null) {
      this.name(other.getName())
          .scalarModel(other.getScalar().orElse(null))
          .referenceModel(other.getReference().orElse(null))
          .compoundModelBuilder()
          .copyOf(other.getCompound().orElse(null))
          .yield()
          .collectionModel(other.getCollection().orElse(null))
          .mapModel(other.getMap().orElse(null))
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