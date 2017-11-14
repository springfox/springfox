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

package springfox.documentation.spring.web.doc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class Serializer {

  private final Map<String, FormatSerializer> formatSerializers;

  public Serializer(List<FormatSerializer> formatSerializers, List<JacksonModuleRegistrar> modules) {

    this.formatSerializers = convertToMap(formatSerializers);

    for (FormatSerializer formatSerializer : formatSerializers) {
      for (JacksonModuleRegistrar each : modules) {
        formatSerializer.maybeRegisterModule(each);
      }
    }

  }

  private static Map<String, FormatSerializer> convertToMap(List<FormatSerializer> formatSerializers) {
    Map<String, FormatSerializer> formatSerializerMap = new HashMap<String, FormatSerializer>();
    for (FormatSerializer formatSerializer : formatSerializers) {
      for (String format : formatSerializer.getSupportedFormats()) {
        formatSerializerMap.put(format, formatSerializer);
      }
    }
    return unmodifiableMap(formatSerializerMap);
  }

  public DocOutput toJson(Object toSerialize) {
    return serialize(toSerialize, "json");
  }

  public DocOutput serialize(Object toSerialize, String format) {
    FormatSerializer formatSerializer = getFormatSerializer(format);
    return new DocOutput(formatSerializer.serialize(toSerialize));
  }

  private FormatSerializer getFormatSerializer(String format) {
    if (!supports(format)) {
      throw new IllegalArgumentException("No serializer registered for " + format);
    }
    return formatSerializers.get(format);
  }

  public boolean supports(String format) {
    return formatSerializers.containsKey(format);
  }

}
