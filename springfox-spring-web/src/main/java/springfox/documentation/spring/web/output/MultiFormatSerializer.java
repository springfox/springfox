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
import org.springframework.http.MediaType;
import springfox.documentation.spring.web.output.formats.CustomFormatOutputMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class MultiFormatSerializer {

  private static final MediaType DEFAULT_MEDIA_TYPE = APPLICATION_JSON;

  private final Map<MediaType, ObjectMapper> mappers = new HashMap<MediaType, ObjectMapper>();

  public MultiFormatSerializer(List<JacksonModuleRegistrar> modules, Collection<CustomFormatOutputMapper> outputMappers) {

    for (CustomFormatOutputMapper mapperWrapper : outputMappers) {
      ObjectMapper objectMapper = mapperWrapper.configureMapper();
      for (JacksonModuleRegistrar each : modules) {
        each.maybeRegisterModule(objectMapper);
      }
      for (MediaType mediaType : mapperWrapper.getFormats()) {
        mappers.put(mediaType, objectMapper);
      }
    }

  }

  public RawOutput toJson(Object toSerialize) {
    return serialize(toSerialize, DEFAULT_MEDIA_TYPE);
  }

  public RawOutput serialize(Object toSerialize, MediaType mediaType) {
    ObjectMapper objectMapper = mappers.get(mediaType);
    if (null == objectMapper) {
      throw new IllegalArgumentException();
    }
    try {
      return new RawOutput(objectMapper.writeValueAsString(toSerialize));
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize object.", e);
    }
  }

  public MediaType findAvailableMediaType(Collection<MediaType> acceptableMediaTypes) {
    if (acceptableMediaTypes.isEmpty()) {
      return DEFAULT_MEDIA_TYPE;
    }
    List<MediaType> sortedMediaTypes = new ArrayList<MediaType>(acceptableMediaTypes);
    MediaType.sortByQualityValue(sortedMediaTypes);
    for (MediaType acceptableMediaType : sortedMediaTypes) {
      for (MediaType availableMediaType : mappers.keySet()) {
        if (acceptableMediaType.includes(availableMediaType)) {
          return availableMediaType;
        }
      }
    }
    return null;
  }

}
