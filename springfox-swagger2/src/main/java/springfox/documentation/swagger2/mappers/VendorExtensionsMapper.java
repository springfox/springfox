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
package springfox.documentation.swagger2.mappers;

import com.google.common.base.Function;
import org.mapstruct.Mapper;
import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.*;
import static com.google.common.collect.FluentIterable.*;
import static com.google.common.collect.Maps.*;

@Mapper
public class VendorExtensionsMapper {

  public Map<String, Object> mapExtensions(List<VendorExtension> from) {
    Map<String, Object> extensions = newTreeMap();
    Iterable<ListVendorExtension> listExtensions = from(from)
        .filter(ListVendorExtension.class);
    for (ListVendorExtension each : listExtensions) {
      extensions.put(each.getName(), each.getValue());
    }
    Iterable<Map<String, Object>> objectExtensions = from(from)
        .filter(ObjectVendorExtension.class)
        .transform(toExtensionMap());
    for (Map<String, Object> each : objectExtensions) {
      extensions.putAll(each);
    }
    Iterable<StringVendorExtension> propertyExtensions = from(from)
        .filter(StringVendorExtension.class);
    for (StringVendorExtension each : propertyExtensions) {
      extensions.put(each.getName(), each.getValue());
    }
    return extensions;
  }

  private Function<ObjectVendorExtension, Map<String, Object>> toExtensionMap() {
    return new Function<ObjectVendorExtension, Map<String, Object>>() {
      @Override
      public Map<String, Object> apply(ObjectVendorExtension input) {
        if (!isNullOrEmpty(input.getName())) {
          Map<String, Object> map = newHashMap();
          map.put(input.getName(), mapExtensions(input.getValue()));
          return map;
        }
        return propertiesAsMap(input);
      }
    };
  }

  private Map<String, Object> propertiesAsMap(ObjectVendorExtension input) {
    Map<String, Object> properties = newHashMap();
    Iterable<StringVendorExtension> stringExtensions = from(input.getValue()).filter(StringVendorExtension.class);
    for (StringVendorExtension property : stringExtensions) {
      properties.put(property.getName(), property.getValue());
    }
    Iterable<ObjectVendorExtension> objectExtensions = from(input.getValue()).filter(ObjectVendorExtension.class);
    for (ObjectVendorExtension property : objectExtensions) {
      properties.put(property.getName(), mapExtensions(property.getValue()));
    }
    return properties;
  }
}
