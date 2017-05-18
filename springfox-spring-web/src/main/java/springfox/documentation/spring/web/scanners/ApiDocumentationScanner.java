/*
 *
 *  Copyright 2015-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.scanners;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.DocumentationBuilder;
import springfox.documentation.builders.ResourceListingBuilder;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.PathAdjuster;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.paths.PathMappingAdjuster;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.service.Tags.*;
import static springfox.documentation.spi.service.contexts.Orderings.*;

@Component
public class ApiDocumentationScanner {

  private ApiListingReferenceScanner apiListingReferenceScanner;
  private ApiListingScanner apiListingScanner;

  @Autowired
  public ApiDocumentationScanner(
      ApiListingReferenceScanner apiListingReferenceScanner,
      ApiListingScanner apiListingScanner) {

    this.apiListingReferenceScanner = apiListingReferenceScanner;
    this.apiListingScanner = apiListingScanner;
  }

  public Documentation scan(DocumentationContext context) {
    ApiListingReferenceScanResult result = apiListingReferenceScanner.scan(context);
    ApiListingScanningContext listingContext = new ApiListingScanningContext(context,
        result.getResourceGroupRequestMappings());

    Multimap<String, ApiListing> apiListings = apiListingScanner.scan(listingContext);
    Set<Tag> tags = toTags(apiListings);
    tags.addAll(context.getTags());
    DocumentationBuilder group = new DocumentationBuilder()
        .name(context.getGroupName())
        .apiListingsByResourceGroupName(apiListings)
        .produces(context.getProduces())
        .consumes(context.getConsumes())
        .host(context.getHost())
        .schemes(context.getProtocols())
        .basePath(context.getPathProvider().getApplicationBasePath())
        .extensions(context.getVendorExtentions())
        .tags(tags);

    Set<ApiListingReference> apiReferenceSet = newTreeSet(listingReferencePathComparator());
    apiReferenceSet.addAll(apiListingReferences(apiListings, context));

    ResourceListing resourceListing = new ResourceListingBuilder()
        .apiVersion(context.getApiInfo().getVersion())
        .apis(from(apiReferenceSet).toSortedList(context.getListingReferenceOrdering()))
        .securitySchemes(context.getSecuritySchemes())
        .info(context.getApiInfo())
        .build();
    group.resourceListing(resourceListing);
    return group.build();
  }

  private Collection<? extends ApiListingReference> apiListingReferences(
      Multimap<String, ApiListing> apiListings,
      DocumentationContext context) {
    Map<String, Collection<ApiListing>> grouped = Multimaps.asMap(apiListings);
    return FluentIterable.from(grouped.entrySet()).transform(toApiListingReference(context)).toSet();
  }

  private Function<Map.Entry<String, Collection<ApiListing>>, ApiListingReference> toApiListingReference(final DocumentationContext context) {
    return new Function<Map.Entry<String, Collection<ApiListing>>, ApiListingReference>() {
      @Override
      public ApiListingReference apply(Map.Entry<String, Collection<ApiListing>> input) {
        String description = Joiner.on(System.getProperty("line.separator"))
            .join(descriptions(input.getValue()));
        PathAdjuster adjuster = new PathMappingAdjuster(context);
        PathProvider pathProvider = context.getPathProvider();
        String path = pathProvider.getResourceListingPath(context.getGroupName(), input.getKey());
        return new ApiListingReference(adjuster.adjustedPath(path), description, 0);
      }
    };
  }

  private Iterable<String> descriptions(Collection<ApiListing> apiListings) {
    return FluentIterable.from(apiListings).transform(toDescription()).toSortedList(Ordering.natural());
  }

  private Function<ApiListing, String> toDescription() {
    return new Function<ApiListing, String>() {
      @Override
      public String apply(ApiListing input) {
        return input.getDescription();
      }
    };
  }

}
