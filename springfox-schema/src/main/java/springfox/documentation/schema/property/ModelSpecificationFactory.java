package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;
import org.springframework.stereotype.Service;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.*;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.AllowableValues;
import springfox.documentation.spi.schema.EnumTypeDeterminer;
import springfox.documentation.spi.schema.contexts.ModelContext;

import java.util.Optional;

import static springfox.documentation.schema.property.PackageNames.safeGetPackageName;

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

  public ModelSpecification create(ModelContext modelContext, ResolvedType resolvedType) {
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
          && enumTypeDeterminer.isEnum(resolvedType
          .getErasedType())) {
        scalar = Optional.of(ScalarType.STRING);
        AllowableValues allowableValues = Enums.allowableValues(resolvedType.getErasedType());
        if (allowableValues instanceof AllowableListValues) {
          enumerationFacet = new EnumerationFacet(((AllowableListValues) allowableValues).getValues());
        }
      } else {
        reference = new ReferenceModelSpecification(
            new ModelKey(
                safeGetPackageName(resolvedType),
                typeNameExtractor.typeName(
                    ModelContext.fromParent(
                        modelContext,
                        resolvedType)),
                modelContext.isReturnType()));
      }
    }
    return new ModelSpecificationBuilder(
        String.format(
            "%s_%s",
            modelContext.getParameterId(),
            typeNameExtractor.typeName(modelContext)))
        .scalarModel(scalar.orElse(null))
        .referenceModel(reference)
        .collectionModel(collectionSpecification)
        .mapModel(mapSpecification)
        .facetsBuilder()
        .enumeration(enumerationFacet)
        .yield()
        .build();
  }

  public TypeNameExtractor getTypeNameExtractor() {
    return typeNameExtractor;
  }

  public EnumTypeDeterminer getEnumTypeDeterminer() {
    return enumTypeDeterminer;
  }
}
