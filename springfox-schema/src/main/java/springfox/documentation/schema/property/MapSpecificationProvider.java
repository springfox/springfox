package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Optional;

import static springfox.documentation.schema.property.PackageNames.*;

class MapSpecificationProvider {
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enums;

  MapSpecificationProvider(
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enums) {
    this.typeNameExtractor = typeNameExtractor;
    this.enums = enums;
  }

  public Optional<MapSpecification> create(
      ModelContext modelContext,
      ResolvedType type) {
    if (Maps.isMapType(type)) {
      ResolvedType keyType = Maps.mapKeyType(type);
      ResolvedType valueType = Maps.mapValueType(type);

      return Optional.of(
          new MapSpecification(
              modelSpecification(
                  modelContext,
                  keyType),
              modelSpecification(
                  modelContext,
                  valueType)));
    }
    return Optional.empty();
  }

  private ModelSpecification modelSpecification(
      ModelContext modelContext,
      ResolvedType type) {
    Optional<ScalarType> scalar
        = ScalarTypes.builtInScalarType(type);

    ReferenceModelSpecification reference = null;
    if (!scalar.isPresent()) {
      if (type != null && enums.isEnum(type.getErasedType())) {
        scalar = Optional.of(ScalarType.STRING);
        //TODO: Enum values in the facet
      } else {
        reference = new ReferenceModelSpecification(
            new ModelKey(
                safeGetPackageName(type),
                typeNameExtractor.typeName(
                    ModelContext.fromParent(
                        modelContext,
                        type)),
                modelContext.isReturnType()));
      }
    }
    return new ModelSpecificationBuilder(String.format(
        "%s_%s",
        modelContext.getParameterId(),
        "String"))
        .scalarModel(scalar.orElse(null))
        .referenceModel(reference)
        .build();
  }
}
