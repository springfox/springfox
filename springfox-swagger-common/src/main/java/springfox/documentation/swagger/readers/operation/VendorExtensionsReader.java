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

package springfox.documentation.swagger.readers.operation;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.List;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.Lists.*;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class VendorExtensionsReader implements OperationBuilderPlugin {

  private static final Logger LOG = LoggerFactory.getLogger(VendorExtensionsReader.class);

  @Override
  public void apply(OperationContext context) {

    Optional<ApiOperation> apiOperation = context.findAnnotation(ApiOperation.class);

    if (apiOperation.isPresent()) {
      Extension[] extensionsAnnotations = apiOperation.get().extensions();
      List<VendorExtension> extensions = readExtensions(extensionsAnnotations);
      LOG.debug("Extension count {} for method {}", extensions.size(), context.getName());
      context.operationBuilder().extensions(extensions);
    }
  }

  private List<VendorExtension> readExtensions(Extension[] vendorAnnotations) {
    return FluentIterable.from(newArrayList(vendorAnnotations))
        .transform(toVendorExtension()).toList();
  }

  private Function<Extension, VendorExtension> toVendorExtension() {
    return new Function<Extension, VendorExtension>() {
      @Override
      public VendorExtension apply(Extension input) {
        return Optional.fromNullable(emptyToNull(input.name()))
            .transform(propertyExtension(input))
            .or(objectExtension(input));
      }
    };
  }

  private VendorExtension objectExtension(Extension each) {
    ObjectVendorExtension extension = new ObjectVendorExtension(ensurePrefixed(nullToEmpty(each.name())));
    for (ExtensionProperty property : each.properties()) {
      if (!isNullOrEmpty(property.name()) && !isNullOrEmpty(property.value())) {
        extension.addProperty(new StringVendorExtension(property.name(), property.value()));
      }
    }
    return extension;
  }

  private Function<String, VendorExtension> propertyExtension(final Extension annotation) {
    return new Function<String, VendorExtension>() {
      @Override
      public VendorExtension apply(String input) {
        ObjectVendorExtension extension = new ObjectVendorExtension(ensurePrefixed(input));
        for (ExtensionProperty each : annotation.properties()) {
          extension.addProperty(new StringVendorExtension(each.name(), each.value()));
        }
        return extension;
      }
    };
  }

  private String ensurePrefixed(String name) {
    if (!isNullOrEmpty(name) && !name.startsWith("x-")) {
      return "x-" + name;
    }
    return name;
  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }
}
