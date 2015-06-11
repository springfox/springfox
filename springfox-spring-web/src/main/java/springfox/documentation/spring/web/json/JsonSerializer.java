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

package springfox.documentation.spring.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonSerializer {
  private ObjectMapper objectMapper = new ObjectMapper();

  public JsonSerializer(List<JacksonModuleRegistrar> modules) {
    for (JacksonModuleRegistrar each : modules) {
      each.maybeRegisterModule(objectMapper);
    }
  }

  public Json toJson(Object toSerialize) {
    try {
      return new Json(objectMapper.writeValueAsString(toSerialize));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not write JSON", e);
    }
  }
}
