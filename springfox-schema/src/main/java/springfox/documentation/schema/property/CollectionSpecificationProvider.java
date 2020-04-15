package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.CollectionType;
import springfox.documentation.schema.Collections;
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

@Component
public class CollectionSpecificationProvider {
  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enums;

  public CollectionSpecificationProvider(
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enums) {
    this.typeNameExtractor = typeNameExtractor;
    this.enums = enums;
  }

  Optional<CollectionSpecification> create(
      ModelContext modelContext,
      ResolvedType collection) {

    if (!Collections.isContainerType(collection)) {
      return Optional.empty();
    }
    ResolvedType itemType = Collections.collectionElementType(collection);
    CollectionType collectionType = Collections.collectionType(collection);
    Optional<ScalarType> scalar
        = ScalarTypes.builtInScalarType(itemType);

    ReferenceModelSpecification reference = null;
    CollectionSpecification collectionSpecification = null;
    if (!scalar.isPresent()) {
      if (itemType != null && enums.isEnum(itemType.getErasedType())) {
        scalar = Optional.of(ScalarType.STRING);
        //TODO: Enum values in the facet
      } else if (Collections.isContainerType(itemType)) {
        collectionSpecification = create(
            modelContext,
            itemType)
            .orElse(null);
      } else {
        reference = new ReferenceModelSpecification(
            new ModelKey(
                safeGetPackageName(itemType),
                typeNameExtractor.typeName(
                    ModelContext.fromParent(
                        modelContext,
                        itemType)),
                modelContext.isReturnType()));
      }
    }

    ModelSpecification itemModel = new ModelSpecificationBuilder(
        String.format(
            "%s_%s",
            modelContext.getParameterId(),
            "String"))
        .scalarModel(scalar.orElse(null))
        .referenceModel(reference)
        .collectionModel(collectionSpecification)
        .build();
    return Optional.of(new CollectionSpecification(itemModel, collectionType));
  }

}
