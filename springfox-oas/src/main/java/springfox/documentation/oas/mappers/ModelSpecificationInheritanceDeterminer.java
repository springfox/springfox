package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import springfox.documentation.schema.CompoundModelSpecification;
import springfox.documentation.schema.ModelKey;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ReferenceModelSpecification;
import springfox.documentation.service.ModelNamesRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelSpecificationInheritanceDeterminer {
  private final ModelNamesRegistry namesRegistry;
  private final Map<ModelKey, Schema> parentLookup = new HashMap<>();

  public ModelSpecificationInheritanceDeterminer(ModelNamesRegistry namesRegistry) {
    this.namesRegistry = namesRegistry;
    for (ModelSpecification each : namesRegistry.modelsByName().values()) {
      Collection<ReferenceModelSpecification> subclasses
          = each.getCompound()
                .map(CompoundModelSpecification::getSubclassReferences)
                .orElse(new ArrayList<>());
      for (ReferenceModelSpecification children : subclasses) {
        toRefModel(each).ifPresent(rm -> parentLookup.put(children.getKey(), rm));
      }
    }
  }

  private Optional<Schema> toRefModel(ModelSpecification each) {
    return each.key()
               .flatMap(k ->
                            namesRegistry.nameByKey(k)
                                         .map(n -> new ObjectSchema().$ref(n).type(null)));
  }

  public Optional<Schema> parent(ModelSpecification source) {
    return source.key().map(parentLookup::get);
  }
}