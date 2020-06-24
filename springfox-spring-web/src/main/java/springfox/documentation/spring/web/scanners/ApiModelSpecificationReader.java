package springfox.documentation.spring.web.scanners;

import com.fasterxml.classmate.TypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import springfox.documentation.schema.ModelSpecification;
import springfox.documentation.schema.ModelSpecificationProvider;
import springfox.documentation.spi.schema.contexts.ModelContext;
import springfox.documentation.spi.service.contexts.RequestMappingContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

import java.util.HashSet;
import java.util.Set;

@Component
public class ApiModelSpecificationReader {
  private final ModelSpecificationProvider modelProvider;
  private final DocumentationPluginsManager pluginsManager;
  private final TypeResolver resolver;

  @Autowired
  public ApiModelSpecificationReader(
      @Qualifier("cachedModels") ModelSpecificationProvider modelProvider,
      DocumentationPluginsManager pluginsManager,
      TypeResolver resolver) {
    this.modelProvider = modelProvider;
    this.pluginsManager = pluginsManager;
    this.resolver = resolver;
  }

  public Set<ModelSpecification> read(RequestMappingContext context) {
    Set<ModelSpecification> specifications = new HashSet<>();
    Set<ModelContext> modelContexts = pluginsManager.modelContexts(context);
    for (ModelContext each : modelContexts) {
      markIgnorablesAsHasSeen(
          context.getIgnorableParameterTypes(),
          each);
      modelProvider.modelSpecificationsFor(each)
          .ifPresent(specifications::add);
      specifications.addAll(modelProvider.modelDependenciesSpecifications(each));
    }
    return specifications;
  }

  private void markIgnorablesAsHasSeen(
      Set<Class> ignorableParameterTypes,
      ModelContext modelContext) {

    for (Class ignorableParameterType : ignorableParameterTypes) {
      modelContext.seen(resolver.resolve(ignorableParameterType));
    }
  }

}
