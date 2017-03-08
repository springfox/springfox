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
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import springfox.documentation.RequestHandler;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListingReference;
import springfox.documentation.service.Operation;
import springfox.documentation.service.ResourceGroup;
import springfox.documentation.spi.service.DocumentationPlugin;

import java.util.Comparator;

import static com.google.common.base.Strings.*;

public class Orderings {
  private Orderings() {
    throw new UnsupportedOperationException();
  }

  public static Comparator<Operation> nickNameComparator() {
    return new Comparator<Operation>() {
      @Override
      public int compare(Operation first, Operation second) {
        return nullToEmpty(first.getUniqueId()).compareTo(nullToEmpty(second.getUniqueId()));
      }
    };
  }

  public static Comparator<Operation> positionComparator() {
    return new Comparator<Operation>() {
      @Override
      public int compare(Operation first, Operation second) {
        return Ints.compare(first.getPosition(), second.getPosition());
      }
    };
  }

  public static Comparator<ApiListingReference> listingReferencePathComparator() {
    return new Comparator<ApiListingReference>() {
      @Override
      public int compare(ApiListingReference first, ApiListingReference second) {
        return first.getPath().compareTo(second.getPath());
      }
    };
  }

  public static Comparator<ApiListingReference> listingPositionComparator() {
    return new Comparator<ApiListingReference>() {
      @Override
      public int compare(ApiListingReference first, ApiListingReference second) {
        return Ints.compare(first.getPosition(), second.getPosition());
      }
    };
  }

  public static Comparator<ApiDescription> apiPathCompatator() {
    return new Comparator<ApiDescription>() {
      @Override
      public int compare(ApiDescription first, ApiDescription second) {
        return first.getPath().compareTo(second.getPath());
      }
    };
  }

  public static Comparator<ResourceGroup> resourceGroupComparator() {
    return new Comparator<ResourceGroup>() {
      @Override
      public int compare(ResourceGroup first, ResourceGroup second) {
        return first.getGroupName().compareTo(second.getGroupName());
      }
    };
  }

  public static Comparator<RequestMappingContext> methodComparator() {
    return new Comparator<RequestMappingContext>() {
      @Override
      public int compare(RequestMappingContext first, RequestMappingContext second) {
        return qualifiedMethodName(first).compareTo(qualifiedMethodName(second));
      }
    };
  }

  private static String qualifiedMethodName(RequestMappingContext context) {
    return String.format("%s.%s", context.getGroupName(), context.getName());
  }


  public static Ordering<RequestHandler> byPatternsCondition() {
    return Ordering.from(new Comparator<RequestHandler>() {
      @Override
      public int compare(RequestHandler first, RequestHandler second) {
        return patternsCondition(first).toString()
            .compareTo(patternsCondition(second).toString());
      }
    });
  }

  public static PatternsRequestCondition patternsCondition(RequestHandler handler) {
    return handler.getPatternsCondition();
  }

  public static Ordering<? super DocumentationPlugin> pluginOrdering() {
    return Ordering.from(byPluginType()).compound(byPluginName());
  }

  public static Comparator<? super DocumentationPlugin> byPluginType() {
    return new Comparator<DocumentationPlugin>() {
      @Override
      public int compare(DocumentationPlugin first, DocumentationPlugin second) {
        return Ints.compare(first.getDocumentationType().hashCode(), second.getDocumentationType().hashCode());
      }
    };
  }

  public static Comparator<? super DocumentationPlugin> byPluginName() {
    return new Comparator<DocumentationPlugin>() {
      @Override
      public int compare(DocumentationPlugin first, DocumentationPlugin second) {
        return first.getGroupName().compareTo(second.getGroupName());
      }
    };
  }
}
