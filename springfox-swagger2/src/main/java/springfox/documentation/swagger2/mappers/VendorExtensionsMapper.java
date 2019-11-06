/*
 *
 *  Copyright 2015-2019 the original author or authors.
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
package springfox.documentation.swagger2.mappers;


import org.mapstruct.Mapper;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static org.springframework.util.StringUtils.*;

@Mapper
public class VendorExtensionsMapper {

  public Map<String, Object> mapExtensions(List<VendorExtension> from) {
    Map<String, Object> extensions = new TreeMap<>();
    Iterable<Map<String, Object>> objectExtensions = from.stream()
        .filter(ObjectVendorExtension.class::isInstance)
        .map(each -> (ObjectVendorExtension) each)
        .map(toExtensionMap()).collect(toList());
    for (Map<String, Object> each : objectExtensions) {
      extensions.putAll(each);
    }

    Iterable<VendorExtension> propertyExtensions = from.stream()
        .filter(each -> !(each instanceof ObjectVendorExtension))
        .collect(toList());
    for (VendorExtension each : propertyExtensions) {
      extensions.put(
          each.getName(),
          each.getValue());
    }
    return extensions;
  }

  private Function<ObjectVendorExtension, Map<String, Object>> toExtensionMap() {
    return input -> {
      if (!isEmpty(input.getName())) {
        Map<String, Object> map = new HashMap<>();
        map.put(
            input.getName(),
            mapExtensions(input.getValue()));
        return map;
      }
      return propertiesAsMap(input);
    };
  }

  private Map<String, Object> propertiesAsMap(ObjectVendorExtension input) {
    Map<String, Object> properties = new HashMap<>();
    Iterable<StringVendorExtension> stringExtensions =
        input.getValue().stream().filter(StringVendorExtension.class::isInstance)
            .map(each -> (StringVendorExtension) each).collect(toList());
    for (StringVendorExtension property : stringExtensions) {
      properties.put(
          property.getName(),
          property.getValue());
    }
    Iterable<ObjectVendorExtension> objectExtensions =
        input.getValue().stream().filter(ObjectVendorExtension.class::isInstance)
            .map(each -> (ObjectVendorExtension) each).collect(toList());
    for (ObjectVendorExtension property : objectExtensions) {
      properties.put(
          property.getName(),
          mapExtensions(property.getValue()));
    }
    return properties;
  }
}
