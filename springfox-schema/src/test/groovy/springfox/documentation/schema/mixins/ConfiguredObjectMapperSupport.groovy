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

package springfox.documentation.schema.mixins

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.databind.ObjectMapper

trait ConfiguredObjectMapperSupport {

  ObjectMapper objectMapperThatUsesFields() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
      .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
      .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
      .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    return objectMapper
  }

  ObjectMapper objectMapperThatUsesGetters() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
    return objectMapper
  }

  ObjectMapper objectMapperThatUsesSetters() {
    def objectMapper = new ObjectMapper()
    objectMapper.serializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    objectMapper.deserializationConfig.defaultVisibilityChecker
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
    return objectMapper
  }
}
