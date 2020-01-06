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

import springfox.documentation.RequestHandler;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.DocumentationPlugin;

import java.util.Comparator;
import java.util.stream.Collectors;

import static java.util.Optional.*;
import static springfox.documentation.RequestHandler.*;

public class Orderings {
  private Orderings() {
    throw new UnsupportedOperationException();
  }

  public static Comparator<Operation> nickNameComparator() {
    return Comparator.comparing(operation -> ofNullable(operation.getUniqueId()).orElse(""));
  }

  public static Comparator<Operation> positionComparator() {
    return Comparator.comparingInt(Operation::getPosition);
  }

  public static Comparator<ApiListingReference> listingReferencePathComparator() {
    return Comparator.comparing(ApiListingReference::getPath);
  }

  public static Comparator<ApiListingReference> listingPositionComparator() {
    return Comparator.comparingInt(ApiListingReference::getPosition);
  }

  public static Comparator<ApiDescription> apiPathCompatator() {
    return Comparator.comparing(ApiDescription::getPath);
  }

  public static Comparator<ResourceGroup> resourceGroupComparator() {
    return Comparator.comparing(Orderings::qualifiedResourceGroupName);
  }

  public static String qualifiedResourceGroupName(ResourceGroup resourceGroup) {
    return String.format("%s.%s.%s", resourceGroup.getGroupName(),
        resourceGroup.getControllerClass().map(cls -> cls.getName()).orElse("-"),
        resourceGroup.getPosition());
  }

  public static Comparator<RequestMappingContext> methodComparator() {
    return Comparator.comparing(Orderings::qualifiedMethodName);
  }

  public static String qualifiedMethodName(RequestMappingContext context) {
    return String.format("%s.%s.%s.%s", context.getGroupName(),
        context.getReturnType().getBriefDescription(), context.getName(),
        methodParametersSignature(context));
  }

  private static String methodParametersSignature(RequestMappingContext context) {
    return context
        .getParameters().stream().map(p -> String.format("%s-%s",
            p.getParameterType().getBriefDescription(), p.getParameterIndex()))
        .collect(Collectors.joining(",", "[", "]"));
  }

  public static Comparator<RequestHandler> byPatternsCondition() {
    return Comparator
        .comparing(requestHandler -> sortedPaths(requestHandler.getPatternsCondition()));
  }

  public static Comparator<RequestHandler> byOperationName() {
    return Comparator.comparing(RequestHandler::getName);
  }

  public static Comparator<? super DocumentationPlugin> pluginOrdering() {
    return byPluginType().thenComparing(byPluginName());
  }

  public static Comparator<DocumentationPlugin> byPluginType() {
    return Comparator
        .comparingInt(documentationPlugin -> documentationPlugin.getDocumentationType().hashCode());
  }

  public static Comparator<DocumentationPlugin> byPluginName() {
    return Comparator.comparing(DocumentationPlugin::getGroupName);
  }
}
