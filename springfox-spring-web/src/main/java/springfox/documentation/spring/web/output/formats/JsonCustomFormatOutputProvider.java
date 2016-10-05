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
package springfox.documentation.spring.web.output.formats;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;

/**
 * @author Alexandru-Constantin Bledea
 * @since Sep 14, 2016
 */
@Component
public class JsonCustomFormatOutputProvider implements CustomFormatOutputMapper {

  private static final String HAL_MEDIA_TYPE = "application/hal+json";

  @Override
  public ObjectMapper configureMapper() {
    return new ObjectMapper();
  }

  @Override
  public Collection<MediaType> getFormats() {
    return asList(APPLICATION_JSON, parseMediaType(HAL_MEDIA_TYPE));
  }

}
