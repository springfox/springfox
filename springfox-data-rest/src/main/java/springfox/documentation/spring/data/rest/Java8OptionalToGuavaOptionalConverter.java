/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.documentation.spring.data.rest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Java8OptionalToGuavaOptionalConverter implements Converter<Object, Optional<?>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Java8OptionalToGuavaOptionalConverter.class);

  @Override
  public Optional<?> convert(Object source) {
    if (source != null) {
      if (isJdk8Optional(source)) {
        try {
          Method optionalIsPresent = source.getClass().getDeclaredMethod("isPresent");
          optionalIsPresent.setAccessible(true);
          Method optionalGet = source.getClass().getDeclaredMethod("get");
          optionalGet.setAccessible(true);
          if ((Boolean) optionalIsPresent.invoke(source)) {
            return Optional.of(optionalGet.invoke(source));
          }
        } catch (NoSuchMethodException e) {
          LOGGER.warn(e.getMessage());
        } catch (IllegalAccessException e) {
          LOGGER.warn(e.getMessage());
        } catch (InvocationTargetException e) {
          LOGGER.warn(e.getMessage());
        }
      } else {
        return Optional.of(source);
      }
      return Optional.absent();
    }
    return Optional.fromNullable(source);
  }

  @VisibleForTesting
  boolean isJdk8Optional(Object source) {
    return "java.util.Optional".equals(source.getClass().getName());
  }
}