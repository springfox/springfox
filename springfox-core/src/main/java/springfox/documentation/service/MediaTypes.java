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
package springfox.documentation.service;

import com.google.common.base.Function;
import com.google.common.base.Predicates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class MediaTypes {
  private static final Logger LOGGER = LoggerFactory.getLogger(MediaTypes.class);
  private MediaTypes() {
    throw new UnsupportedOperationException();
  }

  public static Set<MediaType> toMediaTypes(Set<String> consumes) {
    return consumes.stream()
        .map(parsedMediaType())
        .filter(Predicates.<MediaType>notNull())
        .collect(toSet());
  }

  private static Function<String, MediaType> parsedMediaType() {
    return new Function<String, MediaType>() {
      @Override
      public MediaType apply(String input) {
        try {
          return MediaType.valueOf(input);
        } catch (Exception e) {
          LOGGER.warn(String.format("Unable to parse media type %s", input));
          return null;
        }
      }
    };
  }
}
