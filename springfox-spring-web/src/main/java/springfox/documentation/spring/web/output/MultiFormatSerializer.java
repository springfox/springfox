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

package springfox.documentation.spring.web.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiFormatSerializer {

  private static final String FORMAT_JSON = "json";
  private static final String FORMAT_YML = "yml";
  private static final String FORMAT_YAML = "yaml";

  private static final String FORMAT_DEFAULT = FORMAT_JSON;

  private final Map<String, ObjectMapper> mappers = new HashMap<String, ObjectMapper>();

  public MultiFormatSerializer(List<JacksonModuleRegistrar> modules) {

    ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    ObjectMapper jsonMapper = new ObjectMapper();

    for (JacksonModuleRegistrar each : modules) {
      each.maybeRegisterModule(yamlMapper);
      each.maybeRegisterModule(jsonMapper);
    }

    mappers.put(FORMAT_YML, yamlMapper);
    mappers.put(FORMAT_YAML, yamlMapper);
    mappers.put(FORMAT_JSON, jsonMapper);

  }

  public RawOutput toJson(Object toSerialize) {
    return serialize(toSerialize, FORMAT_JSON);
  }

  public RawOutput serialize(Object toSerialize, String format) {
    String actualFormat = getActualFormat(format);
    ObjectMapper objectMapper = mappers.get(actualFormat);
    try {
      return new RawOutput(objectMapper.writeValueAsString(toSerialize));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Could not write " + actualFormat, e);
    }
  }

  private String getActualFormat(String proposedFormat) {
    if (mappers.containsKey(proposedFormat)) {
      return proposedFormat;
    }
    return FORMAT_DEFAULT;
  }

}
