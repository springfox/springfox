package springdox.documentation.spring.web.readers.operation;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springdox.documentation.service.ResourceGroup;
import springdox.documentation.spi.DocumentationType;
import springdox.documentation.spi.service.OperationBuilderPlugin;
import springdox.documentation.spi.service.ResourceGroupingStrategy;
import springdox.documentation.spi.service.contexts.OperationContext;

import java.util.Set;

@Component
public class OperationTagsReader implements OperationBuilderPlugin {
  private final ResourceGroupingStrategy groupingStrategy;

  @Autowired
  public OperationTagsReader(ResourceGroupingStrategy groupingStrategy) {
    this.groupingStrategy = groupingStrategy;
  }

  @Override
  public void apply(OperationContext context) {
    Set<ResourceGroup> resourceGroups 
            = groupingStrategy.getResourceGroups(context.getRequestMappingInfo(), context.getHandlerMethod());
    FluentIterable<String> tags = FluentIterable
            .from(resourceGroups)
            .transform(toTags());
    context.operationBuilder().tags(tags.toSet());  
  }

  private Function<ResourceGroup, String> toTags() {
    return new Function<ResourceGroup, String>() {
      @Override
      public String apply(ResourceGroup input) {
        return input.getGroupName(); 
      }
    };
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return true;
  }
}
