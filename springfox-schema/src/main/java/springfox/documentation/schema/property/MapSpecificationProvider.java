package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.Maps;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Optional;

class MapSpecificationProvider {
  private final ModelSpecificationFactory models;

  MapSpecificationProvider(
      ModelSpecificationFactory models) {
    this.models = models;
  }

  public Optional<MapSpecification> create(
      ModelContext modelContext,
      ResolvedType type) {
    if (Maps.isMapType(type)) {
      ResolvedType keyType = Maps.mapKeyType(type);
      ResolvedType valueType = Maps.mapValueType(type);

      return Optional.of(
          new MapSpecification(
              models.create(modelContext, keyType),
              models.create(modelContext, valueType)));
    }
    return Optional.empty();
  }
}
