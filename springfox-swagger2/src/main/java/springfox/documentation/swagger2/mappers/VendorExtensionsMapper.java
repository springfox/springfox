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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.mapstruct.Mapper;

import springfox.documentation.service.ListVendorExtension;
import springfox.documentation.service.ObjectVendorExtension;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.util.Strings;


@Mapper
public class VendorExtensionsMapper {

  public Map<String, Object> mapExtensions(List<VendorExtension> from) {
    Map<String, Object> extensions = new TreeMap<>();
    for (VendorExtension each : from) {
      if(each instanceof ListVendorExtension) {
        extensions.put(each.getName(), each.getValue());
      }
      if(each instanceof ObjectVendorExtension) {
        extensions.putAll(toExtensionMap().apply((ObjectVendorExtension)each));
      }
      if(each instanceof StringVendorExtension) {
        extensions.put(each.getName(), each.getValue());
      }
    }
    return extensions;
  }

  private Function<ObjectVendorExtension, Map<String, Object>> toExtensionMap() {
    return new Function<ObjectVendorExtension, Map<String, Object>>() {
      @Override
      public Map<String, Object> apply(ObjectVendorExtension input) {
        if (!Strings.isNullOrEmpty(input.getName())) {
          Map<String, Object> map = new HashMap<>();
          map.put(input.getName(), mapExtensions(input.getValue()));
          return map;
        }
        return propertiesAsMap(input);
      }
    };
  }

  private Map<String, Object> propertiesAsMap(ObjectVendorExtension input) {
    Map<String, Object> properties = new HashMap<>();
    for(VendorExtension each : input.getValue()) {
      if(each instanceof StringVendorExtension) {
        properties.put(each.getName(), each.getValue());
      }
      if(each instanceof ObjectVendorExtension) {
        properties.put(each.getName(), mapExtensions(((ObjectVendorExtension)each).getValue()));
      }
    }
    return properties;
  }
}
