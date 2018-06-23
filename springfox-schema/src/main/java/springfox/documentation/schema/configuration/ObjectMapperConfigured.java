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

package springfox.documentation.schema.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEvent;

public class ObjectMapperConfigured extends ApplicationEvent {
  private final ObjectMapper objectMapper;

  /**
   * Create a new ApplicationEvent.
   *
   * @param source the component that published the event (never {@code null})
   * @param objectMapper object mapper to send to event consumers
   */
  public ObjectMapperConfigured(Object source, ObjectMapper objectMapper) {
    super(source);
    this.source = source;
    this.objectMapper = objectMapper;
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
