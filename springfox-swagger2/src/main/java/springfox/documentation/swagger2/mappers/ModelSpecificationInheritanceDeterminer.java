package springfox.documentation.swagger2.mappers;

import io.swagger.models.RefModel;
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
  private final Map<ModelKey, RefModel> parentLookup = new HashMap<>();

  public ModelSpecificationInheritanceDeterminer(ModelNamesRegistry namesRegistry) {
    this.namesRegistry = namesRegistry;
    for (ModelSpecification each : namesRegistry.modelsByName().values()) {
      Collection<ReferenceModelSpecification> subclasses = each.getCompound()
          .map(CompoundModelSpecification::getSubclassReferences)
          .orElse(new ArrayList<>());
      for (ReferenceModelSpecification children : subclasses) {
        toRefModel(each).ifPresent(rm -> parentLookup.put(children.getKey(), rm));
      }
    }
  }

  private Optional<RefModel> toRefModel(ModelSpecification each) {
    return each.key().flatMap(k -> namesRegistry.nameByKey(k).map(RefModel::new));
  }

  public Optional<RefModel> parent(ModelSpecification source) {
    return source.key().map(parentLookup::get);
  }
}
