/*
 *
 *  Copyright 2015 the original author or authors.
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
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.DocumentationBuilder;
import springfox.documentation.builders.ResourceListingBuilder;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.ResourceListing;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.service.contexts.DocumentationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Sets.*;
import static springfox.documentation.builders.BuilderDefaults.*;
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
    List<ApiListingReference> apiListingReferences = result.getApiListingReferences();
    ApiListingScanningContext listingContext = new ApiListingScanningContext(context, result
        .getResourceGroupRequestMappings());

    Multimap<String, ApiListing> apiListings = apiListingScanner.scan(listingContext);
    DocumentationBuilder group = new DocumentationBuilder()
        .name(context.getGroupName())
        .apiListingsByResourceGroupName(apiListings)
        .produces(context.getProduces())
        .consumes(context.getConsumes())
        .schemes(context.getProtocols())
        .basePath(context.getPathProvider().getApplicationBasePath())
        .tags(toTags(apiListings));

    Set<ApiListingReference> apiReferenceSet = newTreeSet(listingReferencePathComparator());
    apiReferenceSet.addAll(apiListingReferences);

    ResourceListing resourceListing = new ResourceListingBuilder()
        .apiVersion(context.getApiInfo().getVersion())
        .apis(from(apiReferenceSet).toSortedList(context.getListingReferenceOrdering()))
        .securitySchemes(context.getSecuritySchemes())
        .info(context.getApiInfo())
        .build();
    group.resourceListing(resourceListing);
    return group.build();
  }

  private Set<Tag> toTags(Multimap<String, ApiListing> apiListings) {
    List<Tag> tags = from(nullToEmptyMultimap(apiListings).entries())
        .transform(fromEntry()).toList();
    TreeSet<Tag> tagSet = newTreeSet(byTagName());
    tagSet.addAll(tags);
    return tagSet;
  }

  private Comparator<Tag> byTagName() {
    return new Comparator<Tag>() {
      @Override
      public int compare(Tag first, Tag second) {
        return first.getName().compareTo(second.getName());
      }
    };
  }

  private Function<Map.Entry<String, ApiListing>, Tag> fromEntry() {
    return new Function<Map.Entry<String, ApiListing>, Tag>() {
      @Override
      public Tag apply(Map.Entry<String, ApiListing> input) {
        return new Tag(input.getKey(), input.getValue().getDescription());
      }
    };
  }

}
