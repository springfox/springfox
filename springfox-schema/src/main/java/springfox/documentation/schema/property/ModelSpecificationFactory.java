package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.stereotype.Service;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.builders.ReferenceModelSpecificationBuilder;
import springfox.documentation.schema.CollectionSpecification;
import springfox.documentation.schema.EnumerationFacet;
import springfox.documentation.schema.Enums;
import springfox.documentation.schema.MapSpecification;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.schema.ScalarTypes;
import springfox.documentation.schema.TypeNameExtractor;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Optional;

import static springfox.documentation.schema.property.PackageNames.*;

@Service
public class ModelSpecificationFactory {

  private final TypeNameExtractor typeNameExtractor;
  private final EnumTypeDeterminer enumTypeDeterminer;

  public ModelSpecificationFactory(
      TypeNameExtractor typeNameExtractor,
      EnumTypeDeterminer enumTypeDeterminer) {
    this.typeNameExtractor = typeNameExtractor;
    this.enumTypeDeterminer = enumTypeDeterminer;
  }

  public ModelSpecification create(
      ModelContext modelContext,
      ResolvedType resolvedType) {
    ReferenceModelSpecification reference = null;
    CollectionSpecification collectionSpecification =
        new CollectionSpecificationProvider(this)
            .create(
                modelContext,
                resolvedType)
            .orElse(null);

    MapSpecification mapSpecification =
        new MapSpecificationProvider(this)
            .create(
                modelContext,
                resolvedType)
            .orElse(null);

    Optional<ScalarType> scalar = ScalarTypes.builtInScalarType(resolvedType);
    EnumerationFacet enumerationFacet = null;
    if (!scalar.isPresent()
        && collectionSpecification == null
        && mapSpecification == null) {
      if (resolvedType != null
          && enumTypeDeterminer.isEnum(resolvedType.getErasedType())) {
        scalar = Optional.of(ScalarType.STRING);
        AllowableValues allowableValues = Enums.allowableValues(resolvedType.getErasedType());
        if (allowableValues instanceof AllowableListValues) {
          enumerationFacet = new EnumerationFacet(((AllowableListValues) allowableValues).getValues());
        }
      } else {
        reference = new ReferenceModelSpecificationBuilder()
            .key(k -> k.qualifiedModelName(q ->
                q.namespace(safeGetPackageName(resolvedType))
                    .name(
                        typeNameExtractor.typeName(
                            ModelContext.fromParent(
                                modelContext,
                                resolvedType))).build())
                .viewDiscriminator(modelContext.getView().orElse(null))
                .validationGroupDiscriminators(modelContext.getValidationGroups())
                .isResponse(modelContext.isReturnType()))
            .build();
      }
    }
    EnumerationFacet finalEnumerationFacet = enumerationFacet;
    ReferenceModelSpecification finalReference = reference;
    return new ModelSpecificationBuilder()
        .scalarModel(scalar.orElse(null))
        .referenceModel(r -> r.copyOf(finalReference))
        .collectionModel(c -> c.copyOf(collectionSpecification))
        .mapModel(m -> m.copyOf(mapSpecification))
        .facets(f -> f.enumerationFacet(e -> e.copyOf(finalEnumerationFacet)))
        .build();
  }

  public TypeNameExtractor getTypeNameExtractor() {
    return typeNameExtractor;
  }

  public EnumTypeDeterminer getEnumTypeDeterminer() {
    return enumTypeDeterminer;
  }
}
