package springdox.documentation.spring.web.scanners;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springdox.documentation.PathProvider;
import springdox.documentation.RequestHandler;
import springdox.documentation.service.ApiListingReference;
import springdox.documentation.service.ResourceGroup;
import springdox.documentation.spi.service.ResourceGroupingStrategy;
import springdox.documentation.spi.service.contexts.ApiSelector;
import springdox.documentation.spi.service.contexts.DocumentationContext;
import springdox.documentation.spi.service.contexts.RequestMappingContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Multimaps.*;

@Component
public class ApiListingReferenceScanner {

  private static final Logger LOG = LoggerFactory.getLogger(ApiListingReferenceScanner.class);

  public ApiListingReferenceScanResult scan(DocumentationContext context) {
    LOG.info("Scanning for api listing references");

    List<ApiListingReference> apiListingReferences = newArrayList();
    ArrayListMultimap<ResourceGroup, RequestMappingContext> resourceGroupRequestMappings
            = ArrayListMultimap.create();
    Map<ResourceGroup, String> resourceGroupDescriptions = new HashMap<ResourceGroup, String>();
    ApiSelector selector = context.getApiSelector();
    for (RequestMappingHandlerMapping requestMappingHandlerMapping : context.getHandlerMappings()) {
      for (RequestHandler handler : matchingHandlers(requestMappingHandlerMapping, selector)) {
        RequestMappingInfo requestMappingInfo = handler.getRequestMapping();
        HandlerMethod handlerMethod = handler.getHandlerMethod();
        ResourceGroupingStrategy resourceGroupingStrategy = context.getResourceGroupingStrategy();
        Set<ResourceGroup> resourceGroups
                  = resourceGroupingStrategy.getResourceGroups(requestMappingInfo, handlerMethod);
        String handlerMethodName = handlerMethod.getMethod().getName();

        String resourceDescription
                  = resourceGroupingStrategy.getResourceDescription(requestMappingInfo, handlerMethod);
        RequestMappingContext requestMappingContext
                  = new RequestMappingContext(context, requestMappingInfo, handlerMethod);

        LOG.info("Request mapping: {} belongs to groups: [{}] ", handlerMethodName, resourceGroups);
        for (ResourceGroup group : resourceGroups) {
            resourceGroupDescriptions.put(group, resourceDescription);

            LOG.info("Adding resource to group:{} with description:{} for handler method:{}",
                    group, resourceDescription, handlerMethodName);

            resourceGroupRequestMappings.put(group, requestMappingContext);
        }
      }
    }

    for (ResourceGroup resourceGroup : resourceGroupDescriptions.keySet()) {
      String resourceGroupName = resourceGroup.getGroupName();
      String listingDescription = resourceGroupDescriptions.get(resourceGroup);
      Integer position = resourceGroup.getPosition();
      PathProvider pathProvider = context.getPathProvider();
      String path = pathProvider.getResourceListingPath(context.getGroupName(), resourceGroupName);
      LOG.info("Created resource listing Path: {} Description: {} Position: {}",
              path, resourceGroupName, position);
      apiListingReferences.add(new ApiListingReference(path, listingDescription, position));
    }
    List<ApiListingReference> sorted = context.getListingReferenceOrdering().sortedCopy(apiListingReferences);
    return new ApiListingReferenceScanResult(sorted,  asMap(resourceGroupRequestMappings));
  }

  private Set<RequestHandler> matchingHandlers(
      RequestMappingHandlerMapping requestMappingHandlerMapping,
      ApiSelector selector) {
    return FluentIterable
        .from(requestMappingHandlerMapping.getHandlerMethods().entrySet())
            .transform(toRequestHandler())
            .filter(selector.getRequestHandlerSelector())
            .toSet();
  }

  private Function<Entry<RequestMappingInfo, HandlerMethod>, RequestHandler> toRequestHandler() {
    return new Function<Entry<RequestMappingInfo, HandlerMethod>, RequestHandler>() {
      @Override
      public RequestHandler apply(Entry<RequestMappingInfo, HandlerMethod> input) {
        return new RequestHandler(input.getKey(), input.getValue());
      }
    };
  }
}
