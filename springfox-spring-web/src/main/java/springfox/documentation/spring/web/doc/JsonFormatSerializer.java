/*
 *
 *  Copyright 2017 the original author or authors.
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static java.util.Arrays.asList;

public class JsonFormatSerializer implements FormatSerializer {

  private final ObjectMapper mapper;

  public JsonFormatSerializer() {
    this(new ObjectMapper());
  }

  JsonFormatSerializer(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void maybeRegisterModule(JacksonModuleRegistrar registrar) {
    registrar.maybeRegisterModule(mapper);
  }

  @Override
  public String serialize(Object objectToSerialize) {
    try {
      return mapper.writeValueAsString(objectToSerialize);
    } catch (JsonProcessingException e) {
        throw new IllegalArgumentException("Could not serialize object.", e);
    }
  }

  @Override
  public List<String> getSupportedFormats() {
    return asList("json", null);
  }

}
