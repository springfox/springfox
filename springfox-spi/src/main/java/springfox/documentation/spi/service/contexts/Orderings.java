/*
 *
 *  Copyright 2015-2016 the original author or authors.
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

package springfox.documentation.spi.service.contexts;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.DocumentationPlugin;

import java.util.Comparator;

import static com.google.common.base.Strings.*;
import static springfox.documentation.RequestHandler.*;

public class Orderings {
  private Orderings() {
    throw new UnsupportedOperationException();
  }

  public static Comparator<Operation> nickNameComparator() {
    return Comparator.comparing(operation -> nullToEmpty(operation.getUniqueId()));
  }

  public static Comparator<Operation> positionComparator() {
    return (first, second) -> Ints.compare(first.getPosition(), second.getPosition());
  }

  public static Comparator<ApiListingReference> listingReferencePathComparator() {
    return Comparator.comparing(ApiListingReference::getPath);
  }

  public static Comparator<ApiListingReference> listingPositionComparator() {
    return (first, second) -> Ints.compare(first.getPosition(), second.getPosition());
  }

  public static Comparator<ApiDescription> apiPathCompatator() {
    return Comparator.comparing(ApiDescription::getPath);
  }

  public static Comparator<ResourceGroup> resourceGroupComparator() {
    return Comparator.comparing(ResourceGroup::getGroupName);
  }

  public static Comparator<RequestMappingContext> methodComparator() {
    return Comparator.comparing(Orderings::qualifiedMethodName);
  }

  private static String qualifiedMethodName(RequestMappingContext context) {
    return String.format("%s.%s", context.getGroupName(), context.getName());
  }

  public static Ordering<RequestHandler> byPatternsCondition() {
    return Ordering.from(
        Comparator.comparing(requestHandler -> sortedPaths(requestHandler.getPatternsCondition())));
  }

  public static Ordering<RequestHandler> byOperationName() {
    return Ordering.from(Comparator.comparing(RequestHandler::getName));
  }

  public static Ordering<DocumentationPlugin> pluginOrdering() {
    return Ordering.from(byPluginType()).compound(byPluginName());
  }

  public static Comparator<DocumentationPlugin> byPluginType() {
    return (first, second) -> Ints.compare(
        first.getDocumentationType().hashCode(),
        second.getDocumentationType().hashCode());
  }

  public static Comparator<DocumentationPlugin> byPluginName() {
    return Comparator.comparing(DocumentationPlugin::getGroupName);
  }
}
